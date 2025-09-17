package io.github.yalz.ldio.core.pipeline.validation;

import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@Singleton
@Produces
@Primary
public class ValidationExceptionHandler implements io.micronaut.http.server.exceptions.ExceptionHandler<ConstraintViolationException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(v -> "{\"%s\": \"%s\"}".formatted(v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.joining(","));
        return HttpResponse.badRequest(new JsonError("{ \"Validation failed\": [%s]}".formatted(message)));
    }
}