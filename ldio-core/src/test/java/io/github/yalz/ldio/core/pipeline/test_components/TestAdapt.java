package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;

@ComponentName(value = "Test:Adapt", type = ComponentName.ComponentType.ADAPTER)
public class TestAdapt extends EtlAdapter {
    public TestComponent testComponent = new TestComponent();

    public TestAdapt(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Dataset adapt(EtlInput.Content data) {
        return DatasetFactory.create();
    }

    @Override
    public void cleanup() {
        testComponent.clean();
    }
}
