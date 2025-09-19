package io.github.yalz.ldio.core.sink;

import io.github.yalz.ldio.core.pipeline.PipelineDeletedEvent;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SinkServiceTest {

    private RedisCommands<String, String> mockRedis;
    private SinkService sinkService;

    @BeforeEach
    void setUp() {
        mockRedis = mock(RedisCommands.class);
        RedisClient mockClient = mock(RedisClient.class);
        when(mockClient.connect()).thenReturn(mock(StatefulRedisConnection.class));
        when(mockClient.connect().sync()).thenReturn(mockRedis);

        sinkService = new SinkService(mockClient);
    }

    @Test
    void testWriteToSink_CallsXaddWithCorrectKeyAndMessage() {
        sinkService.writeToSink("myPipeline", "some RDF data");

        verify(mockRedis).xadd(
                eq("sink:myPipeline"),
                argThat((Map<String, String> map) -> map.containsKey("data") && map.containsKey("timestamp"))
        );
    }

    @Test
    void testListSinkStreams_FiltersOnlyStreamKeys() {
        ScanCursor cursor = ScanCursor.INITIAL;

        // Create a mock KeyScanCursor
        KeyScanCursor<String> mockScanCursor = mock(KeyScanCursor.class);
        when(mockScanCursor.getKeys()).thenReturn(List.of("sink:one", "sink:two"));
        when(mockScanCursor.isFinished()).thenReturn(true);

        when(mockRedis.scan(eq(cursor), any())).thenReturn(mockScanCursor);
        when(mockRedis.type("sink:one")).thenReturn("stream");
        when(mockRedis.type("sink:two")).thenReturn("none");

        List<String> result = sinkService.listSinkStreams();

        assertEquals(List.of("sink:one"), result);
    }


    @Test
    void testReadAllSinkMessages_FormatsMessagesCorrectly() {
        // Mock the scan result
        KeyScanCursor<String> mockScanCursor = mock(KeyScanCursor.class);
        when(mockScanCursor.getKeys()).thenReturn(List.of("sink:test"));
        when(mockScanCursor.isFinished()).thenReturn(true);

        when(mockRedis.scan(any(ScanCursor.class), any())).thenReturn(mockScanCursor);
        when(mockRedis.type("sink:test")).thenReturn("stream");

        // Mock the stream message
        StreamMessage<String, String> mockMessage = mock(StreamMessage.class);
        when(mockMessage.getId()).thenReturn("123-0");
        when(mockMessage.getBody()).thenReturn(Map.of(
                "timestamp", Instant.now().toString(),
                "data", "hello"
        ));

        when(mockRedis.xrange(eq("sink:test"), any(), any())).thenReturn(List.of(mockMessage));

        // Execute
        Map<String, List<Map<String, Object>>> result = sinkService.readAllSinkMessages(10);

        // Assertions
        assertTrue(result.containsKey("sink:test"));
        Map<String, Object> firstMessage = result.get("sink:test").getFirst();
        assertEquals("123-0", firstMessage.get("id"));
        assertTrue(firstMessage.get("fields").toString().contains("hello"));
    }


    @Test
    void testOnPipelineDeleted_DeletesStreamKey() {
        PipelineDeletedEvent event = new PipelineDeletedEvent("myPipeline");

        sinkService.onPipelineDeleted(event);

        verify(mockRedis).del("sink:myPipeline");
    }
}
