package dev.jora.postman4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.jora.postman4j.junit5.PostmanJunit5Extension;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

/**
 * @author dyadyaJora on 16.02.2025
 */
@ExtendWith(PostmanJunit5Extension.class)
public class RestAssuredJUnit5ExtensionTest {
    private static WireMockServer wireMockServer;
    private final static String WIREMOCK_HOST = "localhost";

    @BeforeAll
    public static void setUp() {

        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_HOST, wireMockServer.port());

        WireMock.stubFor(get("/success")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("OK response body")));

        WireMock.stubFor(get("/server-error")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Server error response")));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://" + WIREMOCK_HOST)
                .setPort(wireMockServer.port()).build();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }


    @Test
    public void firstTest() {
        RestAssured.given()
                .when()
                .get("/success")
                .then()
                .statusCode(200);
    }

    @Test
    public void secondTest() {
        RestAssured.given()
                .when()
                .get("/server-error")
                .then()
                .statusCode(500);
    }
}
