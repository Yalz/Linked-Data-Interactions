package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.component.ComponentRegistry;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

        assertTrue(containsComponentNamed(catalog, "inputs", "Test:In"));
        assertTrue(containsComponentNamed(catalog, "adapters", "Test:Adapt"));
        assertTrue(containsComponentNamed(catalog, "transformers", "Test:Transform"));
        assertTrue(containsComponentNamed(catalog, "outputs", "Test:Out"));
        assertTrue(containsComponentNamed(catalog, "outputs", "Ext:Out"));

        var pipelines = pipelineManager.getPipelines();
        var pipeline = pipelines.values().stream().findFirst().get();

        assertEquals(1, pipeline.outputs.size());
        assertEquals(getClassNameFromGroup(catalog, "outputs", "Ext:Out"), pipeline.outputs.getFirst().getClass().getCanonicalName());
    }

    private boolean containsComponentNamed(Map<String, List<Map<String, Object>>> catalog, String group, String name) {
        return catalog.getOrDefault(group, List.of()).stream()
                .anyMatch(entry -> name.equals(entry.get("name")));
    }

    private String getClassNameFromGroup(Map<String, List<Map<String, Object>>> catalog, String group, String name) {
        return catalog.getOrDefault(group, List.of()).stream()
                .filter(entry -> name.equals(entry.get("name")))
                .map(entry -> (String) entry.get("class"))
                .findFirst()
                .orElseThrow();
    }
}
