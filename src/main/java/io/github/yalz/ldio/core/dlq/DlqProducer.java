package io.github.yalz.ldio.core.dlq;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Singleton;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;

import java.util.Map;

@Singleton
public class DlqProducer {
    public static final String DLQ_STREAM_NAME = "ldio-dlq";
    private final RedisCommands<String, String> syncCommands;

    public DlqProducer(RedisClient redisClient) {
        this.syncCommands = redisClient.connect().sync();
    }

    public void sendToPipeline(String pipeline, String step, Model data, Exception e) {
        Map<String, String> message = Map.of(
                "pipeline", pipeline,
                "step", step,
                "data", RDFWriter.source(data).format(RDFFormat.TTL).asString(),
                "exception", e.toString()
        );
        syncCommands.xadd(DLQ_STREAM_NAME, message);
    }
}
