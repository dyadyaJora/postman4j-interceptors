package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.ConverterUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author dyadyaJora on 11.12.2024
 */
public class InterceptorTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        // Mock responses
        WireMock.stubFor(get("/success")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Success response")));

        WireMock.stubFor(get("/bad-request")
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("Bad request response")));

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
    public void testSuccessResponse() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(new PostmanRequestExecutor())
                .build()) {

            HttpUriRequestBase request = new HttpGet(wireMockServer.url("/success"));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode());
            }
        }
    }

    @Test
    public void testBadRequestResponse() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(new PostmanRequestExecutor())
                .build()) {

            HttpUriRequestBase request = new HttpGet(wireMockServer.url("/bad-request"));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(400, response.getCode());
            }
        }
    }

    @Test
    public void testServerErrorResponse() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(new PostmanRequestExecutor())
                .build()) {

            HttpUriRequestBase request = new HttpGet(wireMockServer.url("/server-error"));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(500, response.getCode());
            }
        }
    }


    @Test
    public void testServerErrorResponseWithExecutor() throws IOException {
        PostmanRequestExecutor interceptor = new PostmanRequestExecutor();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(interceptor)
                .build()) {

            HttpUriRequestBase request = new HttpGet(wireMockServer.url("/server-error"));
            TestAspects.executeRequest(httpClient, request);
            PostmanCollection collection = interceptor.getData().get("My Test Collection");

            System.out.println(ConverterUtils.toJsonString(collection));

            assertEquals("My Test Collection", collection.getInfo().getName());
            assertEquals(1, collection.getItem().size());
            assertEquals("Folder #1", collection.getItem().get(0) .getName());
        }
    }

    @Test
    public void testSlashes() {
        assertEquals("/", PostmanRequestExecutor.trimAndRemoveSlashes("///"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("/aa/bb/"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("aa/bb/"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("/aa/bb"));
    }
}
