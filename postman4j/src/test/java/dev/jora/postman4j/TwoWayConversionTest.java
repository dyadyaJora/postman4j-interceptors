package dev.jora.postman4j;

import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.ConverterUtils;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TwoWayConversionTest {

    private String originalContent;

    @BeforeEach
    public void setUp() throws IOException {
        originalContent = Files.readString(Path.of("src/test/resources/mock_collection.json"), StandardCharsets.UTF_8);
    }

    @Test
    public void testTwoWayConversion(@TempDir Path tempDir) throws IOException, JSONException {
        PostmanCollection postmanCollection = ConverterUtils.fromJsonString(originalContent);
        String convertedContent = ConverterUtils.toJsonString(postmanCollection);

        Path tempFile = tempDir.resolve("mock_collection_converted.json");
        Files.writeString(tempFile, convertedContent, StandardCharsets.UTF_8);

        JSONAssert.assertEquals(originalContent, convertedContent, JSONCompareMode.LENIENT);
    }
}
