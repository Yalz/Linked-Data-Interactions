package io.github.yalz.ldio.core.rdf_writer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public record RdfWriter(Lang contentType) {
    public static final String CONTENT_TYPE_KEY = "rdf-writer.content-type";
    public static final String CONTENT_TYPE_DEFAULT = "text/turtle";

    public String writeToOutputFormat(Model model) {
        return RDFWriter.source(model).lang(contentType).asString();
    }
}
