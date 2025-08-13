package io.github.yalz.ldio.core.pipeline.component.adapter;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

public abstract class EtlAdapter extends EtlComponent {
    public EtlAdapter(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    public abstract String adapt(String data);
}
