package io.github.yalz.ldio.core.pipeline.config;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Serdeable
public class EtlComponentConfig {
    private final String name;
    private final Map<String, String> config;

    public EtlComponentConfig(String name, Map<String, String> config) {
        this.name = name;
        this.config = normalizeKeys(config != null ? config : Map.of());;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public Map<String, String> normalizeKeys(Map<String, String> config) {
        return config.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey()
                                .toLowerCase()
                                .replace("-", "")
                                .replace("_", ""),
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement
                ));
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
