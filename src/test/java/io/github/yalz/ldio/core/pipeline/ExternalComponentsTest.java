package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.component.ComponentRegistry;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "ext")
public class ExternalComponentsTest {

    @Inject
    ComponentRegistry componentRegistry;

    @Inject
    PipelineManager pipelineManager;

    @Test
    void testExternalComponents() {
        var catalog = componentRegistry.getCatalog();
        assertTrue(catalog.getFirst().containsKey("Test:In"));
        assertTrue(catalog.get(1).containsKey("Test:Adapt"));
        assertTrue(catalog.get(2).containsKey("Test:Transform"));
        assertTrue(catalog.getLast().containsKey("Test:Out"));
        assertTrue(catalog.getLast().containsKey("Ext:Out"));

        var pipelines = pipelineManager.getPipelines();
        var pipeline = pipelines.values().stream().findFirst().get();

        assertEquals(1, pipeline.outputs.size());
        assertEquals(catalog.getLast().get("Ext:Out"), pipeline.outputs.getFirst().getClass().getCanonicalName());
    }
}
