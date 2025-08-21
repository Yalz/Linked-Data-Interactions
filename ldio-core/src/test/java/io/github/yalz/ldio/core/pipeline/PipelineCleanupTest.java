package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.test_components.TestAdapt;
import io.github.yalz.ldio.core.pipeline.test_components.TestIn;
import io.github.yalz.ldio.core.pipeline.test_components.TestOut;
import io.github.yalz.ldio.core.pipeline.test_components.TestTransform;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.EnabledIfDockerAvailable;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@MicronautTest(environments = "init")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfDockerAvailable
public class PipelineCleanupTest implements TestPropertyProvider {
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
    PipelineManager manager;

    @Inject
    ApplicationEventPublisher<ApplicationShutdownEvent> eventPublisher;
    @Test
    void testShutdownTriggersCleanup() {
        var pipeline_a = manager.getPipelines().get("testPipeline");

        // Simulate shutdown
        eventPublisher.publishEvent(new ApplicationShutdownEvent(mock(EmbeddedServer.class)));

        // Verify cleanup
        TestIn testIn = (TestIn) pipeline_a.getInput();
        TestAdapt testAdapt = (TestAdapt) testIn.getAdapter();
        TestTransform testTransform = (TestTransform) pipeline_a.transformers.getFirst();
        TestOut testOut = (TestOut) pipeline_a.outputs.getFirst();

        assertTrue(testIn.testComponent.isCleaned());
        assertTrue(testAdapt.testComponent.isCleaned());
        assertTrue(testTransform.testComponent.isCleaned());
        assertTrue(testOut.testComponent.isCleaned());

    }
}
