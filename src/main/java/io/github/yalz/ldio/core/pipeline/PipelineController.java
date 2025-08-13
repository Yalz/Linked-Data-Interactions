package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.OrchestratorConfig;
import io.github.yalz.ldio.core.component.ComponentRegistry;
import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller
public class PipelineController {
    private final PipelineManager pipelineManager;

    @Inject
    OrchestratorConfig config;

    @Inject
    ComponentRegistry componentRegistry;

    public PipelineController(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    @Get("/config")
    String config() {
        return componentRegistry.getCatalog().toString();
    }

    @Post
    String createPipeline(@Body PipelineConfig pipelineConfig) {
        pipelineManager.createPipeline(pipelineConfig);
        return pipelineConfig.getName();
    }
}
