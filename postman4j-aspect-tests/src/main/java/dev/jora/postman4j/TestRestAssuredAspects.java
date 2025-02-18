package dev.jora.postman4j;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import dev.jora.postman4j.annotations.UsePostmanResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.Getter;

/**
 * @author dyadyaJora on 14.02.2025
 */
public class TestRestAssuredAspects {

    @Getter
    private PostmanRestassuredFilter filter;

    public TestRestAssuredAspects(String host, int port) {
        this.filter = new PostmanRestassuredFilter();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://" + host)
                .setPort(port)
                .addFilter(filter).build();
    }

    @UsePostmanCollection("My Test Collection")
    @UsePostmanFolderPath("Folder #1")
    @UsePostmanRequest("My Request")
    @UsePostmanResponse("My Response")
    public Response executeRequest(String path) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .get(path);
    }
}
