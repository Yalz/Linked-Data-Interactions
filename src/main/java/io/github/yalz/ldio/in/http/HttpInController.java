package io.github.yalz.ldio.in.http;

import io.github.yalz.ldio.core.pipeline.PipelineCreatedEvent;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.runtime.event.annotation.EventListener;

import java.util.HashMap;
import java.util.Map;

@Controller("/pipeline")
public class HttpInController {
    private final Map<String, EtlInput> inputs = new HashMap<>();

    @Post("/{pipelineId}")
    public HttpResponse<String> submit(@PathVariable String pipelineId, @Body String data) {
        if (inputs.containsKey(pipelineId)) {
            inputs.get(pipelineId).submit(data);
            return HttpResponse.ok("Submitted");
        }
        return HttpResponse.ok("No pipeline by that name");
    }

    @EventListener
    public void onApplicationEvent(PipelineCreatedEvent event) {
        if (event.input().getClass().equals(HttpInput.class)) {
            inputs.put(event.pipelineId(), event.input());
        }
    }
}
