package io.github.yalz.ldio.out.console;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ComponentName(value = "Ldio:ConsoleOut", type = ComponentName.ComponentType.OUTPUT)
public class ConsoleOut extends EtlOutput {
    Logger logger = LoggerFactory.getLogger(ConsoleOut.class);

    @ComponentProperty(key = "rdf-writer.content-type", defaultValue = "text/turtle", expectedType = Lang.class)
    private Lang lang;

    public ConsoleOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public void handle(Model data) {
        logger.info("pipeline {}: {}", pipelineName, RDFWriter.source(data).lang(lang).asString());
    }
}
