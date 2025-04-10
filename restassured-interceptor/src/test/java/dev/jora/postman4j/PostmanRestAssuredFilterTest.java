package dev.jora.postman4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanSettings;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author dyadyaJora on 31.01.2025
 */
public class PostmanRestAssuredFilterTest {
    private static WireMockServer wireMockServer;
    private final static String WIREMOCK_HOST = "localhost";
    private final static int WIREMOCK_PORT = 7878;

    @BeforeAll
    public static void setUp() {
        wireMockServer = TestUtils.createWireMockServer(WIREMOCK_HOST, WIREMOCK_PORT);
    }
    @Test
    public void testFilter() throws JsonProcessingException {
        PostmanRestassuredFilter filter = new PostmanRestassuredFilter();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://" + WIREMOCK_HOST)
                .setPort(WIREMOCK_PORT)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(filter).build();

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new HashMap<>())
                .post("/success-post");

        assertEquals(200, response.getStatusCode());
        JsonPath jsonPath = response.getBody().jsonPath();
        assertNotNull(jsonPath);

        PostmanCollection collection = filter.getData().get(PostmanSettings.DEFAULT_COLLECTION_NAME);
        assertNotNull(collection);
        System.out.println(ConverterUtils.toJsonString(collection));
        assertEquals(PostmanSettings.DEFAULT_COLLECTION_NAME, collection.getInfo().getName());
        assertEquals(1, collection.getItem().size());

    }

    @Test
    public void testFormDataRequest() throws JsonProcessingException {
        PostmanRestassuredFilter filter = new PostmanRestassuredFilter(UUID.randomUUID().toString(), null);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://" + WIREMOCK_HOST)
                .setPort(WIREMOCK_PORT)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(filter).build();

        Response response = RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("key1", "value1")
                .formParam("key2", "value2")
                .post("/success-post");

        assertEquals(200, response.getStatusCode());
        JsonPath jsonPath = response.getBody().jsonPath();
        assertNotNull(jsonPath);

        PostmanCollection collection = filter.getData().get(PostmanSettings.DEFAULT_COLLECTION_NAME);
        assertNotNull(collection);
        System.out.println(ConverterUtils.toJsonString(collection));
        assertEquals(PostmanSettings.DEFAULT_COLLECTION_NAME, collection.getInfo().getName());
        assertEquals(1, collection.getItem().size());
    }

    @Test
    public void testSuccessGetWithCustomItemNameGenerator() throws JsonProcessingException {
        PostmanSettings settings = PostmanSettings.builder()
                .itemNameGenerator(uri -> "Request for " + getRequestPath(uri))
                .build();

        PostmanRestassuredFilter filter = new PostmanRestassuredFilter(UUID.randomUUID().toString(), settings);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://" + WIREMOCK_HOST)
                .setPort(WIREMOCK_PORT)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(filter).build();

        Response response = RestAssured.given()
                .get("/success");

        assertEquals(200, response.getStatusCode());
        JsonPath jsonPath = response.getBody().jsonPath();
        assertNotNull(jsonPath);

        PostmanCollection collection = filter.getData().get(PostmanSettings.DEFAULT_COLLECTION_NAME);
        assertNotNull(collection);
        System.out.println(ConverterUtils.toJsonString(collection));
        assertEquals(PostmanSettings.DEFAULT_COLLECTION_NAME, collection.getInfo().getName());
        assertEquals(1, collection.getItem().size());
        assertEquals("Request for /success", collection.getItem().get(0).getName());
    }

    public static String getRequestPath(String uriString) {
        try {
            URI uri = new URI(uriString);
            return uri.getPath();
        } catch (URISyntaxException e) {
            return "unknown";
        }
    }
}
