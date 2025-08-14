package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.rdf.model.Model;

@ComponentName(value = "Test:Breaking", type = ComponentName.ComponentType.TRANSFORMER)
public class BreakingTransformer extends EtlTransformer {
    public BreakingTransformer(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Model transform(Model data) {
        throw new IllegalArgumentException("I like trains");
    }
}
