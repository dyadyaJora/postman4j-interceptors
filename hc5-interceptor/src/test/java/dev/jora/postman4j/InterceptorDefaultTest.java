package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.models.RequestClass;
import dev.jora.postman4j.models.RequestUnion;
import dev.jora.postman4j.models.Response;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanSettings;
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
public class InterceptorDefaultTest {

    private static WireMockServer wireMockServer;
    private final static String WIREMOCK_HOST = "localhost";
    private final static int WIREMOCK_PORT = 7878;

    @BeforeAll
    public static void setUp() {
        wireMockServer = TestUtils.createWireMockServer(WIREMOCK_HOST, WIREMOCK_PORT);
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }
    private void executeAndVerifyRequest(String url, int expectedStatusCode, String expectedStatus, String expectedName) throws IOException {
        PostmanRequestExecutor interceptor = new PostmanRequestExecutor();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setRequestExecutor(interceptor)
                .build()) {

            HttpUriRequestBase request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(expectedStatusCode, response.getCode());

                PostmanCollection collection = interceptor.getData().get(PostmanSettings.DEFAULT_COLLECTION_NAME);

                System.out.println(ConverterUtils.toJsonString(collection));

                assertEquals(PostmanSettings.DEFAULT_COLLECTION_NAME, collection.getInfo().getName());
                assertEquals(1, collection.getItem().size());

                Items item = collection.getItem().get(0);

                RequestClass postmanRequest = item.getRequest().getRequestClassValue();
                assertEquals("Request 1", item.getName());
                assertEquals("GET", postmanRequest.getMethod());
                assertEquals(url, postmanRequest.getUrl().getStringValue());

                Response postmanResponse = item.getResponse().get(0);
                assertEquals(expectedStatusCode, postmanResponse.getCode());
                assertEquals(expectedStatus, postmanResponse.getStatus());
                assertEquals(expectedName, postmanResponse.getName());
            }
        }
    }

    @Test
    public void testSuccessResponse() throws IOException {
        executeAndVerifyRequest(wireMockServer.url("/success"), 200, "OK", "200 OK");
    }

    @Test
    public void testBadRequestResponse() throws IOException {
        executeAndVerifyRequest(wireMockServer.url("/bad-request"), 400, "Bad Request", "400 Bad Request");
    }

    @Test
    public void testServerErrorResponse() throws IOException {
        executeAndVerifyRequest(wireMockServer.url("/server-error"), 500, "Server Error", "500 Server Error");
    }

    @Test
    public void testSequentialCalls() {

    }
}
