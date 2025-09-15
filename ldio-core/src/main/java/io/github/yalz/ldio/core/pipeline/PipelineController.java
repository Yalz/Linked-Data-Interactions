package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.component.ComponentRegistry;
import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
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
    String createPipeline(@Body PipelineConfig pipelineConfig) {
        pipelineManager.createPipeline(pipelineConfig);
        return pipelineConfig.getName();
    }

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    Set<String> getActivePipelines() {
        return pipelineManager.getPipelines().keySet();
    }
}
