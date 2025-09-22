package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.component.ComponentRegistry;
import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@Controller("/api")
public class PipelineController {
    private final PipelineManager pipelineManager;

    @Inject
    ComponentRegistry componentRegistry;

    public PipelineController(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    @Get("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<Map<String, Object>>> config() {
        return componentRegistry.getCatalog();
    }

    @Post
    String createPipeline(@Body @Valid PipelineConfig pipelineConfig) {
        pipelineManager.createPipeline(pipelineConfig, true);
        return pipelineConfig.getName();
    }

    @Delete("/{pipeline}")
    void deletePipeline(@PathVariable String pipeline) {
        pipelineManager.deletePipeline(pipeline);
    }

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, PipelineConfig> getActivePipelines() {
        return pipelineManager.getPipelineConfigs();
    }
}
