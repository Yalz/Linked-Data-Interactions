package io.github.yalz.ldio.transformer.noop;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.rdf.model.Model;

@ComponentName(value = "Ldio:NoopTransformer", type = ComponentName.ComponentType.TRANSFORMER,
        description = """
                A basic transformer that has no function whatsoever.
                """)
public class NoopTransformer extends EtlTransformer {
    @ComponentProperty(key = "owner", defaultValue = "ldio", required = true)
    String owner;

    public NoopTransformer(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Model transform(Model data) {
        return null;
    }
}
