package io.github.yalz.ldio.in.http;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import static io.github.yalz.ldio.core.pipeline.component.ComponentName.ComponentType.INPUT;
import static io.github.yalz.ldio.in.http.HttpInEvent.LifecycleEvent.CREATED;

@ComponentName(value = "Ldio:HttpIn", type = INPUT)
public class HttpInput extends EtlInput {
    @Inject
    ApplicationEventPublisher<HttpInEvent> applicationEventPublisher;

    public HttpInput(String pipelineName, InputConfig config) {
        super(pipelineName, config);
    }

    @PostConstruct
    void init() {
        applicationEventPublisher.publishEvent(new HttpInEvent(pipelineName, this, CREATED));
    }
}
