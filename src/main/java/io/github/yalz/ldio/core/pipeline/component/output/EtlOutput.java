package io.github.yalz.ldio.core.pipeline.component.output;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

public abstract class EtlOutput extends EtlComponent {
    public EtlOutput(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    public abstract void handle(String data);
}
