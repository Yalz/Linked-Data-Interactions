package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

@ComponentName(value = "Test:Out", type = ComponentName.ComponentType.OUTPUT)
public class TestOut extends EtlOutput {
    public TestOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public void handle(String data) {

    }
}
