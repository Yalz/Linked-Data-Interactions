package io.github.yalz.ldio.in.http;

public record HttpInEvent(String pipeline, HttpInput input, LifecycleEvent lifecycleEvent) {
    enum LifecycleEvent {
        CREATED, DELETED
    }
}
