package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

/**
 * @author dyadyaJora on 16.12.2024
 */
public class TestUtils {

    public static final String MULTIPART_RESPONSE_BODY_EXAMPLE = "--boundary\r\n"
            + "Content-Disposition: form-data; name=\"field1\"\r\n\r\n"
            + "value1\r\n"
            + "--boundary\r\n"
            + "Content-Disposition: form-data; name=\"file\"; filename=\"example.txt\"\r\n"
            + "Content-Type: text/plain\r\n\r\n"
            + "file content\r\n"
            + "--boundary--";
    public static WireMockServer createWireMockServer(String host, int port) {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
        wireMockServer.start();
        WireMock.configureFor(host, wireMockServer.port());

        // Mock responses for simple get requests
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

        // Mock post responses with json body
        WireMock.stubFor(post("/success-post")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Success response\"}")));

        WireMock.stubFor(post("/bad-request-post")
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Bad request response\"}")));

        WireMock.stubFor(post("/server-error-post")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Server error response\"}")));

        // Mock response with multipart/form-data content

        WireMock.stubFor(post("/multipart-response")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "multipart/form-data; boundary=--boundary")
                        .withBody(MULTIPART_RESPONSE_BODY_EXAMPLE)));

        return wireMockServer;
    }
}
