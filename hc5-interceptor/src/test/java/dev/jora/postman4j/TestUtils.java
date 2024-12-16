package dev.jora.postman4j;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

/**
 * @author dyadyaJora on 16.12.2024
 */
public class TestUtils {

    public static WireMockServer createWireMockServer(String host, int port) {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
        wireMockServer.start();
        WireMock.configureFor(host, wireMockServer.port());

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
        return wireMockServer;
    }
}
