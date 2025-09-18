package io.github.yalz.ldio.core.sink;

import io.github.yalz.ldio.core.pipeline.PipelineDeletedEvent;
import io.lettuce.core.*;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class SinkStreamService {
    private final Logger logger = LoggerFactory.getLogger(SinkStreamService.class);
    private final RedisCommands<String, String> redis;

    public SinkStreamService(RedisClient redisClient) {
        this.redis = redisClient.connect().sync();
    }

    public List<String> listSinkStreams() {
        List<String> sinkKeys = new ArrayList<>();
        ScanCursor cursor = ScanCursor.INITIAL;

        do {
            KeyScanCursor<String> scanCursor = redis.scan(cursor, ScanArgs.Builder.matches("sink:*"));
            cursor = scanCursor;
            for (String key : scanCursor.getKeys()) {
                if ("stream".equals(redis.type(key))) {
                    sinkKeys.add(key);
                }
            }
        } while (!cursor.isFinished());

        return sinkKeys;
    }

    public Map<String, List<Map<String, Object>>> readAllSinkMessages(int count) {
        List<String> streams = listSinkStreams();
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        for (String streamKey : streams) {
            List<StreamMessage<String, String>> messages = redis.xrange(streamKey, Range.create("-", "+"), Limit.from(count));
            List<Map<String, Object>> formatted = messages.stream()
                    .map(msg -> Map.of(
                            "id", msg.getId(),
                            "timestamp", msg.getBody().get("timestamp"),
                            "fields", msg.getBody()
                    ))
                    .collect(Collectors.toList());

            result.put(streamKey, formatted);
        }

        return result;
    }

    @EventListener
    void onPipelineDeleted(PipelineDeletedEvent event) {
        String streamKey = "sink:" + event.pipelineId();
        redis.del(streamKey);

        logger.info("Deleted sink stream for pipeline {}", event.pipelineId());
    }
}
