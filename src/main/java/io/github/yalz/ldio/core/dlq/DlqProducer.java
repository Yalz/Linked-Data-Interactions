package io.github.yalz.ldio.core.dlq;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class DlqProducer {
    private final RedisCommands<String, String> syncCommands;

    public DlqProducer(RedisClient redisClient) {
        this.syncCommands = redisClient.connect().sync();
    }

    public void sendToPipeline(String pipeline, String step, String data) {
        Map<String, String> message = Map.of(
                "pipeline", pipeline,
                "step", step,
                "data", data
        );
        syncCommands.xadd("etl-pipeline", message);
    }
}
