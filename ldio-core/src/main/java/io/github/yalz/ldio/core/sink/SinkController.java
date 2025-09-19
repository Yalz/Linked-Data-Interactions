package io.github.yalz.ldio.core.sink;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@Controller("/sink")
public class SinkController {

    @Inject
    SinkService sinkService;

    @Get("/messages")
    public Map<String, List<Map<String, Object>>> getAllSinkMessages(@QueryValue(defaultValue = "20") int count) {
        return sinkService.readAllSinkMessages(count);
    }
}