package io.github.yalz.ldio.core.pipeline.component.adapter;

import io.github.yalz.ldio.core.pipeline.component.EtlComponent;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.Dataset;

public abstract class EtlAdapter extends EtlComponent {
    public EtlAdapter(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    public abstract Dataset adapt(EtlInput.Content data);
}
