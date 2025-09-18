package io.github.yalz.ldio.out.sink;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.rdf_writer.RdfWriter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.time.Instant;
import java.util.Map;

import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_DEFAULT;
import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_KEY;

@ComponentName(value = "Ldio:SinkOut", type = ComponentName.ComponentType.OUTPUT)
public class SinkOut extends EtlOutput {
    @Inject
    private RedisClient redisClient;

    @ComponentProperty(key = CONTENT_TYPE_KEY, defaultValue = CONTENT_TYPE_DEFAULT, expectedType = Lang.class)
    private Lang contentType;

    private RedisCommands<String, String> syncCommands;

    private final RdfWriter rdfWriter;

    public SinkOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
        rdfWriter = new RdfWriter(contentType);
    }

    @Override
    public void handle(Model data) {
        if (syncCommands == null) {
            syncCommands = redisClient.connectPubSub().sync();
        }
        Map<String, String> message = Map.of(
                "data", rdfWriter.writeToOutputFormat(data),
                "timestamp", Instant.now().toString()
        );
        syncCommands.xadd("sink:" + pipelineName, message);
    }
}
