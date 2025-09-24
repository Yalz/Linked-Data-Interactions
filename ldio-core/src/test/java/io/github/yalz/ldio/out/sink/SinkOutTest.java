package io.github.yalz.ldio.out.sink;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import io.github.yalz.ldio.core.sink.SinkService;
import org.apache.jena.query.Dataset;
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
                "rdf-writer.format", "application/trig"
        ));

        sinkOut = new SinkOut("testPipeline", config);

        Field serviceField = SinkOut.class.getDeclaredField("sinkService");
        serviceField.setAccessible(true);
        serviceField.set(sinkOut, mockSinkService);
    }

    @Test
    void testHandle_CallsSinkServiceWithRdfOutput() {
        Dataset model = RDFParser.fromString("_:b0 <http://schema.org/name> \"Jane Doe\" .", Lang.NQ).toDataset();

        sinkOut.handle(model);

        verify(mockSinkService).writeToSink(
                eq("testPipeline"),
                argThat(output -> output.contains("[ <http://schema.org/name>  \"Jane Doe\" ] ."))
        );
    }
}


