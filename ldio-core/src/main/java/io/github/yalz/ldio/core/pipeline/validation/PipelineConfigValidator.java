package io.github.yalz.ldio.core.pipeline.validation;

import io.github.yalz.ldio.core.pipeline.NoValidComponentException;
import io.github.yalz.ldio.core.pipeline.PipelineManager;
import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.ComponentRegistry;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;

import java.lang.reflect.Field;
import java.util.Map;

@Singleton
public class PipelineConfigValidator implements ConstraintValidator<ValidPipelineConfig, PipelineConfig> {

    private final ComponentRegistry componentRegistry;
    private final PipelineManager pipelineManager;

    public PipelineConfigValidator(ComponentRegistry componentRegistry, PipelineManager pipelineManager) {
        this.componentRegistry = componentRegistry;
        this.pipelineManager = pipelineManager;
    }

    @Override
    public boolean isValid(@Nullable PipelineConfig config,
                           @NonNull AnnotationValue<ValidPipelineConfig> annotationMetadata,
                           @NonNull ConstraintValidatorContext context) {
        if (config == null) return false;

        boolean valid = true;

        if (config.getName() == null || config.getName().isBlank()) {
            context.buildConstraintViolationWithTemplate("Pipeline name must not be blank")
                    .addPropertyNode("name")
                    .addConstraintViolation();
            valid = false;
        }

        if (config.getInput() == null || config.getInput().getName() == null || config.getInput().getName().isBlank()) {
            context.buildConstraintViolationWithTemplate("Input component must have a name")
                    .addPropertyNode("input.name")
                    .addConstraintViolation();
            valid = false;
        }

        if (isInvalidComponent(config.getInput(), ComponentName.ComponentType.INPUT, context)) {
            valid = false;
        }

        if (config.getInput().getAdapter() != null) {
            if (isInvalidComponent(config.getInput(), ComponentName.ComponentType.ADAPTER, context)) {
                valid = false;
            }
        }

        if (!config.getTransformers().isEmpty()) {
            for (var transformer : config.getTransformers()) {
                if (isInvalidComponent(transformer, ComponentName.ComponentType.TRANSFORMER, context)) {
                    valid = false;
                }
            }
        }

        if (config.getOutputs() == null || config.getOutputs().isEmpty()) {
            context.buildConstraintViolationWithTemplate("At least one output component is required")
                    .addPropertyNode("outputs")
                    .addConstraintViolation();
            valid = false;
        }

        if (!config.getOutputs().isEmpty()) {
            for (var output : config.getOutputs()) {
                if (isInvalidComponent(output, ComponentName.ComponentType.OUTPUT, context)) {
                    valid = false;
                }
            }
        }

        if (pipelineManager.getPipelines().containsKey(config.getName())) {
            context.buildConstraintViolationWithTemplate("Pipeline with name %s already exists".formatted(config.getName()))
                    .addPropertyNode("name")
                    .addConstraintViolation();
            valid = false;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
        }

        return valid;
    }

    boolean isInvalidComponent(EtlComponentConfig config, ComponentName.ComponentType componentType, ConstraintValidatorContext context) {
        try {
            var componentClass = componentRegistry.getComponentClass(config, componentType).componentClass();

            for (Field field : componentClass.getDeclaredFields()) {
                ComponentProperty property = field.getAnnotation(ComponentProperty.class);
                if (property != null) {
                    String formattedKey = formattedKey(property.key());
                    if (!config.getConfig().containsKey(formattedKey)) {
                        context.buildConstraintViolationWithTemplate("Component %s is missing property %s.".formatted(config.getName(), property.key()))
                                .addPropertyNode(componentType.name().toLowerCase())
                                .addConstraintViolation();
                        return true;
                    }
                    if (config.getConfig().get(formattedKey).isEmpty() || config.getConfig().get(formattedKey).isBlank()) {
                        context.buildConstraintViolationWithTemplate("Component %s property %s is empty.".formatted(config.getName(), property.key()))
                                .addPropertyNode(componentType.name().toLowerCase())
                                .addConstraintViolation();
                        return true;
                    }
                }
            }
            return false;

        } catch (NoValidComponentException e) {
            context.buildConstraintViolationWithTemplate("%s component '%s' does not exist.".formatted(componentType, config.getName()))
                    .addPropertyNode(componentType.name().toLowerCase())
                    .addConstraintViolation();

            return true;
        }

    }

    private String formattedKey(String property) {
        return property.replace("-", "");
    }
}
