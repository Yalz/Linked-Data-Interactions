package io.github.yalz.ldio.out.http;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.output.EtlOutput;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.rdf_writer.RdfWriter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;

@ComponentName(value = "Ldio:HttpOut", type = ComponentName.ComponentType.OUTPUT,
        description = "Basic component for exporting pipeline output over HTTP")
public class HttpOut extends EtlOutput {
    @ComponentProperty(key = "rdf-writer.format", defaultValue = "application/trig", expectedType = Lang.class)
    private Lang outputFormat;

    @ComponentProperty(key = "endpoint", required = true)
    private String endpoint;

    protected RdfWriter rdfWriter;
    protected OkHttpClient client;

    public HttpOut(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
        client = new OkHttpClient();
        rdfWriter = new RdfWriter(outputFormat);
    }

    @Override
    public void handle(Dataset dataset) throws Exception {
        if (!dataset.isEmpty()) {
            var outputString = rdfWriter.writeToOutputFormat(dataset);

            RequestBody body = RequestBody.create(
                    outputString, MediaType.get(outputFormat.getHeaderString()));

            // Build the request
            Request request = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .build();

            client.newCall(request).execute();
        }
    }
}
