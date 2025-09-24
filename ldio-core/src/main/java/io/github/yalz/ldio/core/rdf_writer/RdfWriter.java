package io.github.yalz.ldio.core.rdf_writer;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public record RdfWriter(Lang contentType) {
    public static final String CONTENT_TYPE_KEY = "rdf-writer.content-type";
    public static final String CONTENT_TYPE_DEFAULT = "application/trig";

    public String writeToOutputFormat(Dataset dataset) {
        return RDFWriter.source(dataset).lang(contentType).asString();
    }
}
