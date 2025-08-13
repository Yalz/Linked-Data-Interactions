package io.github.yalz.ldio.core.component;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Context
public class ComponentRegistry {
    private final Map<String, Class<? extends EtlInput>> inputComponents = new HashMap<>();
    private final Map<String, Class<? extends EtlAdapter>> adapterComponents = new HashMap<>();
    private final Map<String, Class<? extends EtlTransformer>> transformerComponents = new HashMap<>();
    private final Map<String, Class<? extends EtlOutput>> outputComponents = new HashMap<>();

    public ComponentRegistry(@Value("${componentPaths:}") List<String> componentPaths) {
        initComponentsForPath("io.github.yalz.ldio");

        if (!componentPaths.equals(List.of(""))) {
            componentPaths.stream()
                    .filter(path -> path != null && !path.isBlank())
                    .forEach(this::initComponentsForPath);
        }
    }

    public void initComponentsForPath(String componentPath) {
        Reflections reflections = new Reflections(componentPath);

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ComponentName.class);

        for (Class<?> clazz : annotated) {
            ComponentName annotation = clazz.getAnnotation(ComponentName.class);
            switch (annotation.type()) {
                case INPUT -> inputComponents.put(annotation.value(), (Class<? extends EtlInput>) clazz);
                case ADAPTER -> adapterComponents.put(annotation.value(), (Class<? extends EtlAdapter>) clazz);
                case TRANSFORMER -> transformerComponents.put(annotation.value(), (Class<? extends EtlTransformer>) clazz);
                case OUTPUT -> outputComponents.put(annotation.value(), (Class<? extends EtlOutput>) clazz);
            }
        }
    }

    public Class<?> getComponentClass(EtlComponentConfig componentConfig, ComponentName.ComponentType type) {
        var componentClass = switch (type) {
            case INPUT -> inputComponents.get(componentConfig.getName());
            case ADAPTER -> adapterComponents.get(componentConfig.getName());
            case TRANSFORMER -> transformerComponents.get(componentConfig.getName());
            case OUTPUT -> outputComponents.get(componentConfig.getName());
        };

        if (componentClass == null) {
            throw new IllegalArgumentException(String.format("No %s component with name %s", type, componentConfig.getName()));
        }
        return componentClass;
    }

    public List<Map<String, String>> getCatalog() {
        return Stream.of(inputComponents, adapterComponents, transformerComponents, outputComponents)
                .map(singleMap -> singleMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().getName()
                        ))
                )
                .collect(Collectors.toList());
    }
}
