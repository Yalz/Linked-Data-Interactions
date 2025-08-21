package io.github.yalz.ldio.core.dlq;

import io.github.yalz.ldio.core.pipeline.PipelineManager;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.test_components.BreakingTransformer;
import io.github.yalz.ldio.core.pipeline.test_components.BreakingAdapter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.EnabledIfDockerAvailable;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static io.github.yalz.ldio.core.dlq.DlqProducer.DLQ_ADAPT_STREAM_NAME;
import static io.github.yalz.ldio.core.dlq.DlqProducer.DLQ_TRANSFORM_STREAM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(environments = "dlq")
@EnabledIfDockerAvailable
@Testcontainers
public class DlqTest implements TestPropertyProvider {

    @Inject
    PipelineManager pipelineManager;
    @Inject
    RedisClient redisClient;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    private RedisCommands<String, String> syncCommands;

    @Override
    public Map<String, String> getProperties() {
        redis.start(); // Start container before Micronaut context
        String uri = "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379);
        return Map.of("redis.uri", uri); // Inject into Micronaut config
    }

    @BeforeAll
    void setupRedisClient() {
        syncCommands = redisClient.connect().sync();
    }

    @AfterAll
    void teardown() {
        redisClient.shutdown();
    }

    @Test
    void testAdaptDlq() {
        syncCommands.del(DLQ_ADAPT_STREAM_NAME);

        var pipeline = pipelineManager.getPipelines().get("brokenAdapt");
        EtlInput.Content content = new EtlInput.Content("application/n-quads", "_:x <http://schema.org/x> \"Professor\" .");
        pipeline.getInput().submit(content);

        await().until(() -> !syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_ADAPT_STREAM_NAME, "0")).isEmpty());

        List<StreamMessage<String, String>> messages = syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_ADAPT_STREAM_NAME, "0"));
        var message = messages.getFirst();
        assertEquals(content.toString(), message.getBody().get("data"));
        assertEquals(pipeline.getId(), message.getBody().get("pipeline"));
        assertEquals("java.lang.RuntimeException: We'll fix this one day", message.getBody().get("exception"));
    }

    @Test
    void testTransformDlq() {
        syncCommands.del(DLQ_TRANSFORM_STREAM_NAME);

        var pipeline = pipelineManager.getPipelines().get("brokenTransform");
        pipeline.getInput().submit(ModelFactory.createDefaultModel());

        await().until(() -> !syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_TRANSFORM_STREAM_NAME, "0")).isEmpty());

        List<StreamMessage<String, String>> messages = syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_TRANSFORM_STREAM_NAME, "0"));
        var message = messages.getFirst();
        assertEquals(BreakingTransformer.class.getCanonicalName(), message.getBody().get("step"));
        assertEquals("", message.getBody().get("data"));
        assertEquals(pipeline.getId(), message.getBody().get("pipeline"));
        assertEquals("java.lang.IllegalArgumentException: I like trains", message.getBody().get("exception"));
    }


}
