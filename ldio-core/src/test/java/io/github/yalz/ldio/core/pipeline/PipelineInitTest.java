package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.OrchestratorConfig;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(environments = "init")
class PipelineInitTest {

    @Inject
    OrchestratorConfig config;

    @Test
    void testConfigBinding() {
        assertEquals(1, config.getPipelines().size());
        var pipeline = config.getPipelines().getFirst();

        assertEquals("testPipeline", pipeline.getName());

        var expectedInput = new InputConfig("Test:In", Map.of("key", "val"),
                new EtlComponentConfig("Test:Adapt", Map.of("mapping", "mapping.ttl")));

        var expectedTransform = new EtlComponentConfig("Test:Transform", Map.of("bool", "true"));
        var expectedOutput = new EtlComponentConfig("Test:Out", Map.of("content-type", "text/turtle"));

        assertEquals(expectedInput, pipeline.getInput());

        assertEquals(1, pipeline.getTransformers().size());
        assertEquals(expectedTransform, pipeline.getTransformers().getFirst());

        assertEquals(1, pipeline.getOutputs().size());
        assertEquals(expectedOutput, pipeline.getOutputs().getFirst());
    }

}
