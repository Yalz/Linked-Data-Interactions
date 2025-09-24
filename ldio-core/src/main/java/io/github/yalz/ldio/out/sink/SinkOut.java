package io.github.yalz.ldio.out.sink;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.rdf_writer.RdfWriter;
import io.github.yalz.ldio.core.sink.SinkService;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;

import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_DEFAULT;
import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_KEY;

@ComponentName(value = "Ldio:SinkOut", type = ComponentName.ComponentType.OUTPUT,
        description = "Debugging component that allows the user to send the pipeline output to the database.")
public class SinkOut extends EtlOutput {
    @Inject
    private SinkService sinkService;

    @ComponentProperty(key = CONTENT_TYPE_KEY, defaultValue = CONTENT_TYPE_DEFAULT, expectedType = Lang.class)
    private Lang contentType;

    private RedisCommands<String, String> syncCommands;

    private final RdfWriter rdfWriter;

    public SinkOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
        rdfWriter = new RdfWriter(contentType);
    }

    @Override
    public void handle(Dataset data) {
        sinkService.writeToSink(pipelineName, rdfWriter.writeToOutputFormat(data));
    }
}
