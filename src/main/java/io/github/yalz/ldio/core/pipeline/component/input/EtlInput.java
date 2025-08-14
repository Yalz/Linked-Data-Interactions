package io.github.yalz.ldio.core.pipeline.component.input;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.adapter.RdfAdapter;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import reactor.core.publisher.Flux;

public abstract class EtlInput extends EtlComponent {
    private EtlAdapter adapter;
    private RedisPubSubReactiveCommands<String, String> pubSub;
    private final String channelName;

    public EtlInput(String pipelineName, InputConfig config) {
        super(pipelineName, config);
        this.adapter = new RdfAdapter(pipelineName, config);
        this.channelName = "etl:" + pipelineName + ":stream";
    }

    public void initRedis(RedisClient redisClient) {
        pubSub = redisClient.connectPubSub().reactive();
        pubSub.subscribe(channelName).subscribe();
    }

    public EtlInput withAdapter(EtlAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void submit(Content data) {
        Model model = adapter.adapt(data);
        submit(model);
    }

    public void submit(Model model) {
        String nq = serialize(model);
        pubSub.publish(channelName, nq).subscribe(System.out::println);
    }

    public Flux<Model> getStream() {
        return pubSub.observeChannels()
                .filter(msg -> msg.getChannel().equals(channelName))
                .map(msg -> deserialize(msg.getMessage()));
    }

    private String serialize(Model model) {
        return RDFWriter.source(model).lang(Lang.NQ).asString();
    }

    private Model deserialize(String nq) {
        return RDFParserBuilder.create().fromString(nq).lang(Lang.NQ).toModel();
    }

    public record Content(String contentType, String data) {
    }
}
