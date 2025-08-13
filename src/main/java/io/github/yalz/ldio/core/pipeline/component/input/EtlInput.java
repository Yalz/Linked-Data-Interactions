package io.github.yalz.ldio.core.pipeline.component.input;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public abstract class EtlInput extends EtlComponent {
    private EtlAdapter adapter;
    private final Flux<String> flux;
    private FluxSink<String> sink;

    public EtlInput(String pipelineName, InputConfig config) {
        super(pipelineName, config);
        flux = Flux.create(emitter -> this.sink = emitter,
                FluxSink.OverflowStrategy.BUFFER);
    }

    public EtlInput withAdapter(EtlAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void submit(String data) {
        if (sink != null) {
            if (adapter != null) {
                data = adapter.adapt(data);
            }
            sink.next(data);
        }
    }

    public Flux<String> getStream() {
        return flux;
    }
}
