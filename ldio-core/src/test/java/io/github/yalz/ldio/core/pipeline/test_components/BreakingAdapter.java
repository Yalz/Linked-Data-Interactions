package io.github.yalz.ldio.core.pipeline.test_components;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.adapter.EtlAdapter;
import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.Dataset;

@ComponentName(value = "Test:BreakingAdapt", type = ComponentName.ComponentType.ADAPTER)
public class BreakingAdapter extends EtlAdapter {
    public BreakingAdapter(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Dataset adapt(EtlInput.Content data) {
        throw new RuntimeException("We'll fix this one day");
    }
}
