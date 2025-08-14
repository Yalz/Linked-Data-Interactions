package io.github.yalz.ldio.core.dlq;

import io.github.yalz.ldio.core.pipeline.PipelineManager;
import io.github.yalz.ldio.core.pipeline.test_components.BreakingTransformer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static io.github.yalz.ldio.core.dlq.DlqProducer.DLQ_STREAM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(environments = "dlq")
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
    void testDlq() {
        syncCommands.del(DLQ_STREAM_NAME);

        var pipeline = pipelineManager.getPipelines().get("testPipeline");
        pipeline.getInput().submit(ModelFactory.createDefaultModel());

        await().until(() -> !syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_STREAM_NAME, "0")).isEmpty());

        List<StreamMessage<String, String>> messages = syncCommands.xread(XReadArgs.StreamOffset.from(DLQ_STREAM_NAME, "0"));
        var message = messages.getFirst();
        assertEquals(BreakingTransformer.class.getCanonicalName(), message.getBody().get("step"));
        assertEquals("", message.getBody().get("data"));
        assertEquals("testPipeline", message.getBody().get("pipeline"));
        assertEquals("java.lang.IllegalArgumentException: I like trains", message.getBody().get("exception"));
    }


}
