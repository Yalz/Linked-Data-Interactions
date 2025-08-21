package io.github.yalz.ldio.core.pipeline.test_components;

public class TestComponent {
    boolean cleaned;

    public boolean isCleaned() {
        return cleaned;
    }

    void clean() {
        this.cleaned = true;
    }
}
