package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

@ComponentName(value = "Test:Transform", type = ComponentName.ComponentType.TRANSFORMER)
public class TestTransform extends EtlTransformer {
    public TestTransform(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public String transform(String data) {
        return data;
    }
}
