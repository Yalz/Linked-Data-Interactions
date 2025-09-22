package io.github.yalz.ldio.out.console;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.rdf_writer.RdfWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_DEFAULT;
import static io.github.yalz.ldio.core.rdf_writer.RdfWriter.CONTENT_TYPE_KEY;

@ComponentName(value = "Ldio:ConsoleOut", type = ComponentName.ComponentType.OUTPUT,
        description = "Basic component for debugging a pipeline output by writing it to the console output.")
public class ConsoleOut extends EtlOutput {
    Logger logger = LoggerFactory.getLogger(ConsoleOut.class);

    @ComponentProperty(key = CONTENT_TYPE_KEY, defaultValue = CONTENT_TYPE_DEFAULT, expectedType = Lang.class)
    private Lang lang;

    private final RdfWriter rdfWriter;

    public ConsoleOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
        rdfWriter = new RdfWriter(lang);
    }

    @Override
    public void handle(Model data) {
        logger.info("pipeline {}: {}", pipelineName, rdfWriter.writeToOutputFormat(data));
    }
}
