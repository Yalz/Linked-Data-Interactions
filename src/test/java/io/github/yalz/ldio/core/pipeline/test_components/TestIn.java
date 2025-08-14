package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.InputConfig;

@ComponentName(value = "Test:In", type = ComponentName.ComponentType.INPUT)
public class TestIn extends EtlInput {
    public TestIn(String pipelineName, InputConfig config) {
        super(pipelineName, config);
    }
}
