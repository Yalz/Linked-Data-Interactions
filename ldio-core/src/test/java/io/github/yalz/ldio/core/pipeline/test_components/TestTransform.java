package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.Dataset;

@ComponentName(value = "Test:Transform", type = ComponentName.ComponentType.TRANSFORMER)
public class TestTransform extends EtlTransformer {
    public TestComponent testComponent = new TestComponent();

    public TestTransform(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Dataset transform(Dataset data) {
        return data;
    }

    @Override
    public void cleanup() {
        testComponent.clean();
    }
}
