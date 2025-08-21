package io.github.yalz.ldio.core.pipeline.component;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Context
public class ComponentRegistry {
    private final Map<String, Class<?>> inputComponents = new HashMap<String, Class<?>>();
    private final Map<String, Class<?>> adapterComponents = new HashMap<String, Class<?>>();
    private final Map<String, Class<?>> transformerComponents = new HashMap<String, Class<?>>();
    private final Map<String, Class<?>> outputComponents = new HashMap<String, Class<?>>();

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
                case INPUT -> inputComponents.put(annotation.value(), clazz);
                case ADAPTER -> adapterComponents.put(annotation.value(), clazz);
                case TRANSFORMER -> transformerComponents.put(annotation.value(), clazz);
                case OUTPUT -> outputComponents.put(annotation.value(), clazz);
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

    public Map<String, List<Map<String, Object>>> getCatalog() {
        Map<String, List<Map<String, Object>>> groupedCatalog = new LinkedHashMap<>();

        groupedCatalog.put("inputs", buildCatalogEntries(inputComponents));
        groupedCatalog.put("adapters", buildCatalogEntries(adapterComponents));
        groupedCatalog.put("transformers", buildCatalogEntries(transformerComponents));
        groupedCatalog.put("outputs", buildCatalogEntries(outputComponents));

        return groupedCatalog;
    }

    private List<Map<String, Object>> buildCatalogEntries(Map<String, Class<?>> componentMap) {
        return componentMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> catalogEntry = new HashMap<>();
                    Class<?> componentClass = entry.getValue();

                    catalogEntry.put("name", entry.getKey());
                    catalogEntry.put("class", componentClass.getName());

                    var properties = extractComponentProperties(componentClass);
                    if (!properties.isEmpty()) {
                        catalogEntry.put("properties", properties);
                    }

                    return catalogEntry;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> extractComponentProperties(Class<?> componentClass) {
        List<Map<String, Object>> properties = new ArrayList<>();

        for (Field field : componentClass.getDeclaredFields()) {
            ComponentProperty prop = field.getAnnotation(ComponentProperty.class);
            if (prop != null) {
                Map<String, Object> propInfo = new HashMap<>();
                propInfo.put("key", prop.key());
                propInfo.put("defaultValue", prop.defaultValue());
                propInfo.put("required", prop.required());
                propInfo.put("expectedType", prop.expectedType().getName());
                properties.add(propInfo);
            }
        }

        return properties;
    }
}
