package io.github.yalz.ldio.core.dlq;

import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Singleton;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class DlqProducer {
    public static final String DLQ_ADAPT_STREAM_NAME = "ldio-adapt-dlq";
    public static final String DLQ_TRANSFORM_STREAM_NAME = "ldio-transform-dlq";

    private final Logger logger = LoggerFactory.getLogger(DlqProducer.class);
    private final RedisCommands<String, String> syncCommands;

    public DlqProducer(RedisClient redisClient) {
        this.syncCommands = redisClient.connect().sync();
    }

    public void sentNonAdaptableInput(String pipeline, EtlInput.Content content, Exception e) {
        Map<String, String> message = Map.of(
                "pipeline", pipeline,
                "data", content.toString(),
                "exception", e.toString()
        );
        syncCommands.xadd(DLQ_ADAPT_STREAM_NAME, message);
        logger.error("Pipeline [{}]: Could not adapt item. Sent to DLQ {}", pipeline, DLQ_ADAPT_STREAM_NAME);
    }

    public void sendToDlq(String pipeline, String step, Model data, Exception e) {
        Map<String, String> message = Map.of(
                "pipeline", pipeline,
                "step", step,
                "data", RDFWriter.source(data).format(RDFFormat.TTL).asString(),
                "exception", e.toString()
        );
        syncCommands.xadd(DLQ_TRANSFORM_STREAM_NAME, message);
        logger.error("Pipeline [{}]: Could not transform item. Sent to DLQ {}", pipeline, DLQ_TRANSFORM_STREAM_NAME);
    }
}
