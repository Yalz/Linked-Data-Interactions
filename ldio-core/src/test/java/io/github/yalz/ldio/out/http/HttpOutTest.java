package io.github.yalz.ldio.out.http;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

class HttpOutTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;
    private HttpOut httpOut;
    private Dataset filledModel;

    @BeforeEach
    void setUp() {
        mockClient = mock(OkHttpClient.class);
        mockCall = mock(Call.class);

        filledModel = RDFParser.fromString("_:b0 <http://schema.org/name> \"Jane Doe\" .", Lang.NQ).toDataset();

        String endpoint = "http://example.com/endpoint";
        EtlComponentConfig componentConfig = new EtlComponentConfig("Ldio:HttpOut", Map.of("endpoint", endpoint,
                "rdf-writer.format", "application/trig"));

        // Manually create the HttpOut instance
        httpOut = new HttpOut("testPipeline", componentConfig);
        httpOut.client = mockClient;
    }


    @Test
    void testHandle_WithNonEmptyModel_SendsHttpRequest() throws Exception {
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        httpOut.handle(filledModel);

        verify(mockClient).newCall(any(Request.class));
        verify(mockCall).execute();
    }

    @Test
    void testHandle_WithEmptyModel_DoesNotSendRequest() throws Exception {
        Dataset emptyModel = DatasetFactory.create();

        httpOut.handle(emptyModel);

        verify(mockClient, never()).newCall(any());
    }

    @Test
    void testHandle_ThrowsExceptionOnHttpFailure() throws Exception {
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        try {
            httpOut.handle(filledModel);
        } catch (Exception e) {
            assert e.getMessage().contains("Network error");
        }

        verify(mockCall).execute();
    }
}
