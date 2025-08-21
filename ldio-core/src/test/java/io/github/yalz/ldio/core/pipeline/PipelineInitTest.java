package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.OrchestratorConfig;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.EnabledIfDockerAvailable;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(environments = "init")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfDockerAvailable
class PipelineInitTest implements TestPropertyProvider {
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    @Override
    public Map<String, String> getProperties() {
        redis.start(); // Start container before Micronaut context
        String uri = "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379);
        return Map.of("redis.uri", uri); // Inject into Micronaut config
    }

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
