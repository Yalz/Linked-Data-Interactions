package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

@ComponentName(value = "Test:Adapt", type = ComponentName.ComponentType.ADAPTER)
public class TestAdapt extends EtlAdapter {
    public TestAdapt(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public String adapt(String data) {
        return "";
    }
}
