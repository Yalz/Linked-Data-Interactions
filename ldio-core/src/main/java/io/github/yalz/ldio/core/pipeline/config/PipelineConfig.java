package io.github.yalz.ldio.core.pipeline.config;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public class PipelineConfig {
    private String name;
    private InputConfig input;
    private List<EtlComponentConfig> transformers;
    private List<EtlComponentConfig> outputs;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public InputConfig getInput() { return input; }
    public void setInput(InputConfig input) { this.input = input; }

    public List<EtlComponentConfig> getTransformers() { return transformers; }
    public void setTransformers(List<EtlComponentConfig> transformers) { this.transformers = transformers; }

    public List<EtlComponentConfig> getOutputs() { return outputs; }
    public void setOutputs(List<EtlComponentConfig> outputs) { this.outputs = outputs; }

    @Override
    public String toString() {
        return "PipelineConfig{" +
                "name='" + name + '\'' +
                ", input=" + input +
                ", transformers=" + transformers +
                ", outputs=" + outputs +
                '}';
    }
}
