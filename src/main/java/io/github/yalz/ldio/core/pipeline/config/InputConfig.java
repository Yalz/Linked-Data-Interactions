package io.github.yalz.ldio.core.pipeline.config;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;
import java.util.Objects;

@Serdeable
public class InputConfig extends EtlComponentConfig {
    private EtlComponentConfig adapter;

    public InputConfig(String name, Map<String, String> config, EtlComponentConfig adapter) {
        super(name, config);
        this.adapter = adapter;
    }

    public EtlComponentConfig getAdapter() {
        return adapter;
    }

    public void setAdapter(EtlComponentConfig adapter) {
        this.adapter = adapter;
    }

    @Override
    public String toString() {
        return "InputConfig{" +
                "name='" + getName() + '\'' +
                super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InputConfig that = (InputConfig) o;
        return Objects.equals(adapter, that.adapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adapter);
    }
}
