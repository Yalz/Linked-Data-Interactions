package io.github.yalz.ldio.core.pipeline.config;

import io.github.yalz.ldio.core.pipeline.EtlPipeline;
import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.validation.ValidPipelineConfig;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@ValidPipelineConfig
public class PipelineConfig {
    private String name;
    private InputConfig input;
    private List<EtlComponentConfig> transformers;
    private List<EtlComponentConfig> outputs;

    public static PipelineConfig fromPipeline(EtlPipeline pipeline) {
        var pipelineConfig = new PipelineConfig();
        pipelineConfig.setName(pipeline.getId());
        EtlInput input = pipeline.getInput();
        EtlAdapter adapter = input.getAdapter();
        EtlComponentConfig adapterConfig = null;
        if (adapter != null) {
            adapterConfig = adapter.getConfig();
        }
        pipelineConfig.setInput(new InputConfig(pipeline.getInput().getConfig().getName(), input.getConfig().getRawConfig(), adapterConfig));
        pipelineConfig.setTransformers(pipeline.getTransformers().stream().map(EtlComponent::getConfig).toList());
        pipelineConfig.setOutputs(pipeline.getOutputs().stream().map(EtlComponent::getConfig).toList());

        return pipelineConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputConfig getInput() {
        return input;
    }

    public void setInput(InputConfig input) {
        this.input = input;
    }

    public List<EtlComponentConfig> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<EtlComponentConfig> transformers) {
        this.transformers = transformers;
    }

    public List<EtlComponentConfig> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<EtlComponentConfig> outputs) {
        this.outputs = outputs;
    }

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
