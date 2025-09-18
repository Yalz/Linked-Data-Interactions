package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.OrchestratorConfig;
import io.github.yalz.ldio.core.dlq.DlqProducer;
import io.github.yalz.ldio.core.pipeline.component.ComponentName.ComponentType;
import io.github.yalz.ldio.core.pipeline.component.ComponentRegistry;
import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;
import io.github.yalz.ldio.core.pipeline.repository.RedisPipelineRepository;
import io.lettuce.core.RedisClient;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.yalz.ldio.core.pipeline.component.ComponentName.ComponentType.*;

@Context
@Singleton
public class PipelineManager {

    private final BeanContext beanContext;
    private final ComponentRegistry componentRegistry;
    private final ApplicationEventPublisher<PipelineCreatedEvent> creationEventPublisher;
    private final ApplicationEventPublisher<PipelineDeletedEvent> deletionEventPublisher;

    @Inject
    OrchestratorConfig orchestratorConfig;
    @Inject
    RedisClient redisClient;
    @Inject
    DlqProducer dlqProducer;
    @Inject
    RedisPipelineRepository repository;
    // Track active pipelines by name
    private final Map<String, EtlPipeline> pipelines = new ConcurrentHashMap<>();

    public PipelineManager(BeanContext beanContext, ComponentRegistry componentRegistry, ApplicationEventPublisher<PipelineCreatedEvent> creationEventPublisher, ApplicationEventPublisher<PipelineDeletedEvent> deletionEventPublisher) {
        this.beanContext = beanContext;
        this.componentRegistry = componentRegistry;
        this.creationEventPublisher = creationEventPublisher;
        this.deletionEventPublisher = deletionEventPublisher;
    }

    public void createPipeline(PipelineConfig pipelineConfig) {
        if (repository.hasKey(pipelineConfig.getName())) {
            throw new IllegalStateException("Pipeline already registered: " + pipelineConfig.getName());
        }

        var input = (EtlInput) getComponent(pipelineConfig.getName(), pipelineConfig.getInput(), INPUT);
        input.initRedis(redisClient);
        input.initDlq(dlqProducer);
        beanContext.inject(input);

        var adapter = Optional.ofNullable(pipelineConfig.getInput().getAdapter())
                .map(componentConfig ->
                        (EtlAdapter) getComponent(pipelineConfig.getName(), componentConfig, ADAPTER));

        if (adapter.isPresent()) {
            input = input.withAdapter(adapter.get());
        }

        var transformers = Optional.ofNullable(pipelineConfig.getTransformers())
                .orElseGet(List::of)
                .stream()
                .map(componentConfig ->
                        (EtlTransformer) getComponent(pipelineConfig.getName(), componentConfig, TRANSFORMER))
                .toList();

        var outputs = pipelineConfig.getOutputs()
                .stream()
                .map(componentConfig ->
                        (EtlOutput) getComponent(pipelineConfig.getName(), componentConfig, OUTPUT))
                .toList();

        EtlPipeline pipeline = new EtlPipeline(pipelineConfig.getName(), input, transformers, outputs);
        beanContext.inject(pipeline);

        pipeline.init();

        creationEventPublisher.publishEvent(new PipelineCreatedEvent(pipelineConfig.getName(), input));
        pipelines.put(pipelineConfig.getName(), pipeline);

        try {
            repository.save(pipelineConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, EtlPipeline> getPipelines() {
        return pipelines;
    }

    public Map<String, PipelineConfig> getPipelineConfigs() {
        return repository.findAll();
    }

    private EtlComponent getComponent(String pipelineName, EtlComponentConfig componentConfig, ComponentType componentType) {
        try {
            var etlComponent = componentRegistry.getComponentClass(componentConfig, componentType)
                    .componentClass()
                    .getConstructor(String.class, componentConfig.getClass())
                    .newInstance(pipelineName, componentConfig);
            beanContext.inject(etlComponent);
            return (EtlComponent) etlComponent;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onStartup(ApplicationStartupEvent event) {
        if (repository.findAll() != null) {
            var savedPipelines = repository.findAll();
            savedPipelines.values().forEach(this::createPipeline);

            orchestratorConfig.getPipelines()
                    .stream()
                    .filter(config -> !savedPipelines.containsKey(config.getName()))
                    .forEach(this::createPipeline);
        }
    }

    @EventListener
    public void onShutdown(ApplicationShutdownEvent event) {
        pipelines.values().forEach(EtlPipeline::cleanup);
    }

    @EventListener
    public void onPipelineDeleted(PipelineDeletedEvent event) {
        EtlPipeline pipeline = pipelines.remove(event.pipelineId());
        if (pipeline != null) {
            repository.delete(event.pipelineId());
            pipeline.cleanup();
            beanContext.destroyBean(pipeline);
        }
    }

    public void deletePipeline(String pipeline) {
        deletionEventPublisher.publishEvent(new PipelineDeletedEvent(pipeline));
    }
}


