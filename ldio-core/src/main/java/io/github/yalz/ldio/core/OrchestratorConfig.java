package io.github.yalz.ldio.core;

import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("orchestrator")
public class OrchestratorConfig {
    private List<PipelineConfig> pipelines = new ArrayList<>();

    public List<PipelineConfig> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineConfig> pipelines) {
        this.pipelines = pipelines;
    }
}
