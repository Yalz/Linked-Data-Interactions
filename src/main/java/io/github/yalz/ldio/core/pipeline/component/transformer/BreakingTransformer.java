package io.github.yalz.ldio.core.pipeline.component.transformer;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.rdf.model.Model;

@ComponentName(value = "Ldio:Breaking", type = ComponentName.ComponentType.TRANSFORMER)
public class BreakingTransformer extends EtlTransformer {
    public BreakingTransformer(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Model transform(Model data) {
        throw new IllegalArgumentException("I like trains");
    }
}
