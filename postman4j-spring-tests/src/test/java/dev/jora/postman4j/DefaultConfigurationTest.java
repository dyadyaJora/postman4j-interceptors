package dev.jora.postman4j;

import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanSettings;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static dev.jora.postman4j.endpoints.EchoController.ECHO_COLLECTION_NAME;
import static dev.jora.postman4j.endpoints.EchoController.ECHO_ERROR_COLLECTION_NAME;
import static dev.jora.postman4j.endpoints.SecondController.SECOND_COLLECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author dyadyaJora on 05.01.2025
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {DefaultConfigurationTest.TestConfig.class})
@ActiveProfiles("test")
class DefaultConfigurationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Profile("test")
        public PostmanSettings postmanSettings() {
            return PostmanSettings.builder().logWhenChanged(true).build();
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostmanSettings postmanSettings;

    @Autowired
    private PostmanMiddlewareFilter postmanMiddlewareFilter;

    @BeforeEach
    public void setup() {
        postmanMiddlewareFilter.clean();
    }

    @Test
    void shouldReturnDefaultUnnamedCollection() throws Exception {
        String res = this.restTemplate.getForObject("http://localhost:" + port + "/default",
                String.class);
        assertNotNull(res);
        assertNotNull(postmanMiddlewareFilter.getData());
        assertNotNull(postmanMiddlewareFilter.getData().get("Generated Collection"));
        String expectedCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_generated_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get("Generated Collection")), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturn3SequentialRequestsInOneCollection() throws Exception {

        for (int i = 1; i <= 3; i++) {
            Map<String, String> params = new HashMap<>();
            params.put("retry", String.valueOf(i));
            String res = this.restTemplate.getForObject("http://localhost:" + port + "/default?retry={retry}", String.class, params);
            assertNotNull(res);
        }
        assertNotNull(postmanMiddlewareFilter.getData());
        assertNotNull(postmanMiddlewareFilter.getData().get("Generated Collection"));
        String expectedCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_3_with_query_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get("Generated Collection")), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnDefaultNamedCollection() throws Exception {
        String res = this.restTemplate.getForObject("http://localhost:" + port + "/echo",
                String.class);
        assertNotNull(res);
        assertNotNull(postmanMiddlewareFilter.getData());
        assertNotNull(postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME));
        String expectedCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_echo_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME)), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnDefaultNamedCollectionWith2SequentialRequests() throws Exception {
        String res = this.restTemplate.getForObject("http://localhost:" + port + "/echo", String.class);
        assertNotNull(res);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String requestBody = "{\"content\": \"my-content\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/echo", HttpMethod.POST, requestEntity, String.class);

        assertNotNull(response);
        assertNotNull(postmanMiddlewareFilter.getData());
        PostmanCollection postmanCollection = postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME);
        assertNotNull(postmanCollection);
        assertThat(postmanCollection.getItem()).hasSize(2);

        assertThat(postmanCollection.getInfo().getName()).isEqualTo(ECHO_COLLECTION_NAME);

        String expectedCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_echo_2_folders_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME)), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnDefaultNamedCollectionWithException() throws Exception {
        String res = this.restTemplate.getForObject("http://localhost:" + port + "/echo-error",
                String.class);
        assertNotNull(res);
        assertNotNull(postmanMiddlewareFilter.getData());
        assertThat(postmanMiddlewareFilter.getData()).hasSize(1);
        assertNotNull(postmanMiddlewareFilter.getData().get(ECHO_ERROR_COLLECTION_NAME));
        String expectedCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_echo_exception_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(ECHO_ERROR_COLLECTION_NAME)), JSONCompareMode.LENIENT);
    }


    @Test
    void shouldReturnMultipleCollections() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Map<String, String> params = new HashMap<>();
            params.put("message", String.valueOf(i));
            String res1 = this.restTemplate.getForObject("http://localhost:" + port + "/echo?message={message}", String.class, params);
            assertNotNull(res1);
            String res2 = this.restTemplate.getForObject("http://localhost:" + port + "/second?message={message}", String.class, params);
            assertNotNull(res2);
        }
        assertNotNull(postmanMiddlewareFilter.getData());
        assertNotNull(postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME));
        assertNotNull(postmanMiddlewareFilter.getData().get(SECOND_COLLECTION_NAME));
        String expectedEchoCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_multicollection_echo_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedEchoCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(ECHO_COLLECTION_NAME)), JSONCompareMode.LENIENT);
        String expectedSecondCollection = getExpectedWithPort(Files.readString(Path.of("src/test/resources/default_multicollection_second_collection.json"), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedSecondCollection, ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(SECOND_COLLECTION_NAME)), JSONCompareMode.LENIENT);
    }

    private String getExpectedWithPort(String templateContent) {
        Map<String, String> valuesMap = Map.of(
                "port", String.valueOf(port)
        );

        StringSubstitutor substitution = new StringSubstitutor(valuesMap);
        return substitution.replace(templateContent);
    }
}
