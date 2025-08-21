package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.test_components.TestAdapt;
import io.github.yalz.ldio.core.pipeline.test_components.TestIn;
import io.github.yalz.ldio.core.pipeline.test_components.TestOut;
import io.github.yalz.ldio.core.pipeline.test_components.TestTransform;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@MicronautTest(environments = "init")
public class PipelineCleanupTest {

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
