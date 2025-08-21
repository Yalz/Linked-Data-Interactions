package io.github.yalz.ldio.core.pipeline.component.adapter;

import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.atlas.web.MediaType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class RdfAdapter extends EtlAdapter {
    public RdfAdapter(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Model adapt(EtlInput.Content data) {
        return RDFParser.fromString(data.data(), nameToLang(MediaType.createFromContentType(data.contentType()).getContentTypeStr()))
                .toModel();
    }
}
