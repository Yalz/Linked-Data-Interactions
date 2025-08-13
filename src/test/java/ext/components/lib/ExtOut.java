package ext.components.lib;

import io.github.yalz.ldio.core.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;

@ComponentName(value = "Ext:Out", type = ComponentName.ComponentType.OUTPUT)
public class ExtOut extends EtlOutput {
    public ExtOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public void handle(String data) {

    }
}
