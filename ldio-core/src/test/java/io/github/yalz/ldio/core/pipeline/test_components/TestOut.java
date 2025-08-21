package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.rdf.model.Model;

@ComponentName(value = "Test:Out", type = ComponentName.ComponentType.OUTPUT)
public class TestOut extends EtlOutput {
    public TestComponent testComponent = new TestComponent();

    public TestOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public void handle(Model data) {

    }

    @Override
    public void cleanup() {
        testComponent.clean();
    }
}
