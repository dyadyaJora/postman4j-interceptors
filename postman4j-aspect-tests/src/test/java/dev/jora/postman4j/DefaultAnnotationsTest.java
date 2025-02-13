package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.models.Response;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author dyadyaJora on 16.12.2024
 */
public class DefaultAnnotationsTest {

    private static WireMockServer wireMockServer;
    private final static String WIREMOCK_HOST = "localhost";

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_HOST, wireMockServer.port());

        WireMock.stubFor(get("/server-error")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Server error response")));
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testAnnotationBasedDefaultSettingsWithHc5Executor() throws IOException {
        PostmanRequestExecutor interceptor = new PostmanRequestExecutor();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(interceptor)
                .build()) {

            HttpUriRequestBase request = new HttpGet(wireMockServer.url("/server-error"));
            TestAspects.executeRequest(httpClient, request);
            PostmanCollection collection = interceptor.getData().get("My Test Collection");

            assertEquals("My Test Collection", collection.getInfo().getName());
            assertEquals(1, collection.getItem().size());
            assertEquals("Folder #1", collection.getItem().get(0) .getName());

            Items item = collection.getItem().get(0).getItem().get(0);
            assertEquals("My Request", item.getName());

            Response response = collection.getItem().get(0).getItem().get(0).getResponse().get(0);
            assertEquals(500, response.getCode());
            assertEquals("My Response", response.getName());
            assertEquals("Server Error", response.getStatus());
        }
    }

    @Test
    public void testAnnotationBasedDefaultSettingsWithRestAssuredInterceptor() {
        TestRestAssuredAspects testRestAssuredAspects = new TestRestAssuredAspects(WIREMOCK_HOST, wireMockServer.port());
        testRestAssuredAspects.executeRequest("/server-error");
        PostmanCollection collection = testRestAssuredAspects.getFilter().getData().get("My Test Collection");

        assertEquals("My Test Collection", collection.getInfo().getName());
        assertEquals(1, collection.getItem().size());
        assertEquals("Folder #1", collection.getItem().get(0) .getName());

        Items item = collection.getItem().get(0).getItem().get(0);
        assertEquals("My Request", item.getName());

        Response response = collection.getItem().get(0).getItem().get(0).getResponse().get(0);
        assertEquals(500, response.getCode());
        assertEquals("My Response", response.getName());
//        assertEquals("Server Error", response.getStatus());
    }
}
