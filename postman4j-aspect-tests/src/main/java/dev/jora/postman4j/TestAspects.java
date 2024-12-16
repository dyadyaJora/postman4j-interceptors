package dev.jora.postman4j;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import dev.jora.postman4j.annotations.UsePostmanResponse;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.IOException;

/**
 * @author dyadyaJora on 12.12.2024
 */
class TestAspects {
    @UsePostmanCollection("My Test Collection")
    @UsePostmanFolderPath("Folder #1")
    @UsePostmanRequest("My Request")
    @UsePostmanResponse("My Response")
    public static void executeRequest(CloseableHttpClient httpClient, HttpUriRequestBase request) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println(response.getCode());
        }
    }
}
