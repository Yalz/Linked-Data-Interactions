package io.github.yalz.ldio.core.pipeline.validation;

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

        valid &= validateRequired(config.getName(), "Pipeline name must not be blank", context, "name");
        valid &= validateRequired(config.getInput(), "Input component must be provided", context, "input");
        valid &= validateRequired(config.getInput().getName(), "Input component must have a name", context, "input.name");

        valid &= validateComponent(config.getInput(), ComponentName.ComponentType.INPUT, context);

        if (config.getInput().getAdapter() != null) {
            valid &= validateComponent(config.getInput(), ComponentName.ComponentType.ADAPTER, context);
        }

        for (var transformer : config.getTransformers()) {
            valid &= validateComponent(transformer, ComponentName.ComponentType.TRANSFORMER, context);
        }

        if (config.getOutputs() == null || config.getOutputs().isEmpty()) {
            context.buildConstraintViolationWithTemplate("At least one output component is required")
                    .addPropertyNode("outputs")
                    .addConstraintViolation();
            valid = false;
        } else {
            for (var output : config.getOutputs()) {
                valid &= validateComponent(output, ComponentName.ComponentType.OUTPUT, context);
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

    private boolean validateRequired(@Nullable Object value, String message, ConstraintValidatorContext context, String property) {
        if (value == null || (value instanceof String str && str.isBlank())) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(property)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateComponent(EtlComponentConfig config, ComponentName.ComponentType type, ConstraintValidatorContext context) {
        try {
            var componentClass = componentRegistry.getComponentClass(config, type).componentClass();

            for (Field field : componentClass.getDeclaredFields()) {
                ComponentProperty property = field.getAnnotation(ComponentProperty.class);
                if (property == null) continue;

                String key = formattedKey(property.key());
                String value = config.getConfig().get(key);

                if (value == null || value.isBlank()) {
                    context.buildConstraintViolationWithTemplate("Component %s property %s is missing or empty.".formatted(config.getName(), property.key()))
                            .addPropertyNode(type.name().toLowerCase())
                            .addConstraintViolation();
                    return false;
                }
            }

        } catch (InvalidComponentException e) {
            context.buildConstraintViolationWithTemplate("%s component '%s' does not exist.".formatted(type, config.getName()))
                    .addPropertyNode(type.name().toLowerCase())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private String formattedKey(String property) {
        return property.replace("-", "");
    }
}