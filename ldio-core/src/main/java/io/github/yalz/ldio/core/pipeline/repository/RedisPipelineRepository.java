package io.github.yalz.ldio.core.pipeline.repository;

import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Singleton
public class RedisPipelineRepository implements PipelineRepository {

    private final RedisCommands<String, String> redis;
    private final ObjectMapper objectMapper;

    public RedisPipelineRepository(RedisClient redisClient, ObjectMapper objectMapper) {
        this.redis = redisClient.connect().sync();
        this.objectMapper = objectMapper;
    }

    private String key(String name) {
        return "pipeline:" + name;
    }

    @Override
    public boolean hasKey(String name) {
        return redis.exists(key(name)) > 0;
    }

    @Override
    public PipelineConfig findOne(String name) throws IOException {
        String json = redis.get(key(name));
        if (json == null) {
            throw new NoSuchElementException("Pipeline not found: " + name);
        }
        return objectMapper.readValue(json, PipelineConfig.class);
    }

    @Override
    public Map<String, PipelineConfig> findAll() {
        Map<String, PipelineConfig> result = new HashMap<>();
        ScanCursor cursor = ScanCursor.INITIAL;

        do {
            KeyScanCursor<String> scan = redis.scan(cursor, io.lettuce.core.ScanArgs.Builder.matches("pipeline:*"));
            cursor = scan;

            for (String redisKey : scan.getKeys()) {
                String json = redis.get(redisKey);
                if (json != null) {
                    try {
                        PipelineConfig config = objectMapper.readValue(json, PipelineConfig.class);
                        result.put(config.getName(), config);
                    } catch (IOException ignored) {}
                }
            }
        } while (!cursor.isFinished());

        return result;
    }

    @Override
    public void save(PipelineConfig pipelineConfig) throws IOException {
        String json = objectMapper.writeValueAsString(pipelineConfig);
        redis.set(key(pipelineConfig.getName()), json);
    }

    @Override
    public void delete(String name) {
        redis.del(key(name));
    }
}
