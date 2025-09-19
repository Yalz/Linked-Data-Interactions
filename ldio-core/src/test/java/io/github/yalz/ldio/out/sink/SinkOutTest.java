package io.github.yalz.ldio.out.sink;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.sink.SinkService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.Mockito.*;

class SinkOutTest {

    private SinkService mockSinkService;
    private SinkOut sinkOut;

    @BeforeEach
    void setUp() throws Exception {
        mockSinkService = mock(SinkService.class);

        EtlComponentConfig config = new EtlComponentConfig("Ldio:SinkOut", Map.of(
                "rdf-writer.format", "text/turtle"
        ));

        sinkOut = new SinkOut("testPipeline", config);

        Field serviceField = SinkOut.class.getDeclaredField("sinkService");
        serviceField.setAccessible(true);
        serviceField.set(sinkOut, mockSinkService);
    }

    @Test
    void testHandle_CallsSinkServiceWithRdfOutput() {
        Model model = RDFParser.fromString("_:b0 <http://schema.org/name> \"Jane Doe\" .", Lang.NQ).toModel();

        sinkOut.handle(model);

        verify(mockSinkService).writeToSink(
                eq("testPipeline"),
                argThat(output -> output.contains("[ <http://schema.org/name>  \"Jane Doe\" ] ."))
        );
    }
}


