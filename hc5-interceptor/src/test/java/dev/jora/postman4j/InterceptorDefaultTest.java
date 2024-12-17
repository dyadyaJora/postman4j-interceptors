package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.models.RequestClass;
import dev.jora.postman4j.models.Response;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanSettings;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static dev.jora.postman4j.TestUtils.MULTIPART_RESPONSE_BODY_EXAMPLE;
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

    @Test
    public void testSuccessGetResponse() throws IOException {
        executeAndVerifyRequest("GET", wireMockServer.url("/success"), null, 200, "OK", "200 OK", "Success response");
    }

    @Test
    public void testBadRequestGetResponse() throws IOException {
        executeAndVerifyRequest("GET", wireMockServer.url("/bad-request"), null, 400, "Bad Request", "400 Bad Request", "Bad request response");
    }

    @Test
    public void testServerErrorGetResponse() throws IOException {
        executeAndVerifyRequest("GET", wireMockServer.url("/server-error"), null, 500, "Server Error", "500 Server Error", "Server error response");
    }

    @Test
    public void testSuccessPostResponse() throws IOException {
        executeAndVerifyRequest("POST", wireMockServer.url("/success-post"), "{\"id\": 1}", 200, "OK", "200 OK", "{\"message\": \"Success response\"}");
    }


    @Test
    public void testSuccessPostMultipartFormResponse() throws IOException {
        executeAndVerifyRequest("POST", wireMockServer.url("/multipart-response"), "{\"id\": 1}", 200, "OK", "200 OK", MULTIPART_RESPONSE_BODY_EXAMPLE);
    }

    private void executeAndVerifyRequest(String method, String url, String requestBody, int expectedStatusCode, String expectedStatus, String expectedName, String expectedResponseBody) throws IOException {
        PostmanRequestExecutor interceptor = new PostmanRequestExecutor();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .disableContentCompression()
                .setRequestExecutor(interceptor)
                .build()) {

            HttpUriRequestBase request;
            if ("POST".equalsIgnoreCase(method)) {
                request = new HttpPost(url);
                if (requestBody != null) {
                    request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
                }
            } else {
                request = new HttpGet(url);
            }

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(expectedStatusCode, response.getCode());
                if (requestBody != null) {
                    assertEquals(requestBody, EntityUtils.toString(request.getEntity()));
                }
//                assertEquals(expectedResponseBody, EntityUtils.toString(response.getEntity()));

                PostmanCollection collection = interceptor.getData().get(PostmanSettings.DEFAULT_COLLECTION_NAME);

                System.out.println(ConverterUtils.toJsonString(collection));

                assertEquals(PostmanSettings.DEFAULT_COLLECTION_NAME, collection.getInfo().getName());
                assertEquals(1, collection.getItem().size());

                Items item = collection.getItem().get(0);

                RequestClass postmanRequest = item.getRequest().getRequestClassValue();
                assertEquals("Request 1", item.getName());
                assertEquals(method, postmanRequest.getMethod());
                assertEquals(url, postmanRequest.getUrl().getStringValue());

                Response postmanResponse = item.getResponse().get(0);
                assertEquals(expectedStatusCode, postmanResponse.getCode());
                assertEquals(expectedStatus, postmanResponse.getStatus());
                assertEquals(expectedName, postmanResponse.getName());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
