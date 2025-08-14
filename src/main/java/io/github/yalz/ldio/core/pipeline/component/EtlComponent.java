package io.github.yalz.ldio.core.pipeline.component;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.atlas.web.MediaType;
import org.apache.jena.riot.Lang;

import java.lang.reflect.Field;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

public abstract class EtlComponent {
    protected final String pipelineName;

    public EtlComponent(String pipelineName, EtlComponentConfig config) {
        this.pipelineName = pipelineName;
        injectComponentProperties(this, config);
    }

    public static void injectComponentProperties(Object target, EtlComponentConfig config) {
        for (Field field : target.getClass().getDeclaredFields()) {
            ComponentProperty prop = field.getAnnotation(ComponentProperty.class);
            if (prop != null) {
                String key = prop.key()
                        .toLowerCase()
                        .replace("-", "")
                        .replace("_", "");
                String rawValue = config.getConfig().get(key);

                if (rawValue == null) {
                    if (prop.required()) {
                        throw new IllegalArgumentException("Missing required config key: " + key);
                    }
                    rawValue = prop.defaultValue();
                }

                Object converted = convert(rawValue, prop.expectedType());
                field.setAccessible(true);
                try {
                    field.set(target, converted);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject property: " + key, e);
                }
            }
        }
    }

    private static Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == Lang.class) return nameToLang(MediaType.createFromContentType(value).getContentTypeStr());
        // Add more converters as needed
        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }
}
