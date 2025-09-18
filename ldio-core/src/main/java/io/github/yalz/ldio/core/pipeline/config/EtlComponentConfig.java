package io.github.yalz.ldio.core.pipeline.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Serdeable
public class EtlComponentConfig {
    private final String name;
    private final Map<String, String> config;

    public EtlComponentConfig(String name, Map<String, Object> config) {
        this.name = name;
        this.config = normalizeKeys(flatten(config != null ? config : Map.of()));
    }

    public EtlComponentConfig(String name) {
        this.name = name;
        this.config = Map.of();
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    @JsonIgnore
    public Map<String, Object> getRawConfig() {
        return config.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (Object) entry.getValue()
                ));
    }

    public Map<String, String> normalizeKeys(Map<String, String> config) {
        return config.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey()
                                .toLowerCase()
                                .replace("-", "")
                                .replace("_", ""),
                        Map.Entry::getValue,
                        (_, replacement) -> replacement
                ));
    }

    private static Map<String, String> flatten(Map<String, ?> input) {
        Map<String, String> result = new HashMap<>();
        flattenRecursive("", input, result);
        return result;
    }

    private static void flattenRecursive(String prefix, Map<String, ?> map, Map<String, String> result) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenRecursive(key, (Map<String, ?>) value, result);
            } else {
                result.put(key, String.valueOf(value));
            }
        }
    }

    @Override
    public String toString() {
        return "name='%s', config=%s".formatted(name, config);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EtlComponentConfig that = (EtlComponentConfig) o;
        return Objects.equals(name, that.name) && config.equals(that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, config);
    }
}
