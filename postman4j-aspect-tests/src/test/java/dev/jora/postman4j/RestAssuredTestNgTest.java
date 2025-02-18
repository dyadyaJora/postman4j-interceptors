package dev.jora.postman4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.jora.postman4j.testng.PostmanTestNgMethodListener;
import dev.jora.postman4j.testng.PostmanTestNgSuiteListener;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@Listeners({PostmanTestNgMethodListener.class, PostmanTestNgSuiteListener.class})
public class RestAssuredTestNgTest {
    private static WireMockServer wireMockServer;
    private final static String WIREMOCK_HOST = "localhost";

    @BeforeClass
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

    @AfterClass
    public static void tearDown() throws JsonProcessingException {
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