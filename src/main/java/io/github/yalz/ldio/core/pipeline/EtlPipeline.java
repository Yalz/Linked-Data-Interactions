package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.dlq.DlqProducer;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;

import java.util.List;

@Prototype
public class EtlPipeline implements LifeCycle<EtlPipeline> {

    @Inject
    private DlqProducer dlqProducer;
    protected final String id;
    protected final EtlInput input;
    protected final List<EtlTransformer> transformers;
    protected final List<EtlOutput> outputs;

    public EtlPipeline(String id, EtlInput input, List<EtlTransformer> transformers, List<EtlOutput> outputs) {
        this.id = id;
        this.input = input;
        this.transformers = transformers;
        this.outputs = outputs;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public @NonNull EtlPipeline start() {
        input.getStream()
                .map(this::transform)
                .doOnNext(this::output)
                .subscribe();

        return LifeCycle.super.start();
    }

    private String transform(String input) {
        String transformedData = input;
        for (EtlTransformer transformer: transformers) {
            try {
                transformedData = transformer.transform(transformedData);
            } catch (Exception e) {
                dlqProducer.sendToPipeline(id, transformer.getClass().getCanonicalName(), transformedData);
                throw new RuntimeException(e);
            }

        }
        return transformedData;
    }

    private void output(String input) {
        outputs.forEach(etlOutput -> etlOutput.handle(input));
    }

    @Override
    public @NonNull EtlPipeline stop() {
        return LifeCycle.super.stop();
    }

    @Override
    public void close() {
        LifeCycle.super.close();
    }

    public void init() {
        start();
    }

    public void cleanup() {

    }
}
