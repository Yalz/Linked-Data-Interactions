package io.github.yalz.ldio.core.pipeline.component.input;

import io.github.yalz.ldio.core.dlq.DlqProducer;
import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.adapter.RdfAdapter;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import reactor.core.publisher.Flux;

public abstract class EtlInput extends EtlComponent {
    @Inject
    private DlqProducer dlqProducer;
    private EtlAdapter adapter;
    private RedisPubSubReactiveCommands<String, String> pubSub;
    private final String channelName;

    public EtlInput(String pipelineName, InputConfig config) {
        super(pipelineName, config);
        this.adapter = new RdfAdapter(pipelineName, new EtlComponentConfig("LdioInternal:RdfAdapter"));
        this.channelName = "etl:" + pipelineName + ":stream";
    }

    public void initRedis(RedisClient redisClient) {
        pubSub = redisClient.connectPubSub().reactive();
        pubSub.subscribe(channelName).subscribe();
    }

    public void initDlq(DlqProducer dlqProducer) {
        this.dlqProducer = dlqProducer;
    }

    public EtlInput withAdapter(EtlAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void submit(Content data) {
        try {
            Dataset model = adapter.adapt(data);
            submit(model);
        } catch (Exception e) {
            dlqProducer.sentNonAdaptableInput(pipelineName, data, e);
        }
    }

    public void submit(Dataset model) {
        String nq = serialize(model);
        pubSub.publish(channelName, nq).subscribe();
    }

    public Flux<Dataset> getStream() {
        return pubSub.observeChannels()
                .filter(msg -> msg.getChannel().equals(channelName))
                .map(msg -> deserialize(msg.getMessage()));
    }

    public EtlAdapter getAdapter() {
        return adapter;
    }

    private String serialize(Dataset model) {
        return RDFWriter.source(model).lang(Lang.NQ).asString();
    }

    private Dataset deserialize(String nq) {
        return RDFParserBuilder.create().fromString(nq).lang(Lang.NQ).toDataset();
    }

    public record Content(String contentType, String data) {
    }
}
