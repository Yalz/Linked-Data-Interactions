package io.github.yalz.ldio.core.pipeline.component;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

public class EtlComponent {
    protected final String pipelineName;

    public EtlComponent(String pipelineName, EtlComponentConfig config) {
        this.pipelineName = pipelineName;
    }
}
