package io.github.yalz.ldio.core.pipeline.component.transformer;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

public abstract class EtlTransformer extends EtlComponent {
    public EtlTransformer(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    public abstract String transform(String data);
}
