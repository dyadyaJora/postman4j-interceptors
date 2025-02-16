package dev.jora.postman4j.junit5;

import dev.jora.postman4j.PostmanRestassuredFilter;
import dev.jora.postman4j.utils.ConverterUtils;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author dyadyaJora on 16.02.2025
 */
public class PostmanJunit5Extension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private static final String GENERATED_COLLECTIONS_DIR = "postman4j-generated";
    private final PostmanRestassuredFilter postmanRestassuredFilter;
    public PostmanJunit5Extension() {
        postmanRestassuredFilter = new PostmanRestassuredFilter();
        RestAssured.filters(postmanRestassuredFilter);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String currentCollectionName = postmanRestassuredFilter.getCollectionName();
        String currentCollection = ConverterUtils.toJsonString(postmanRestassuredFilter.getData().get(currentCollectionName));
        saveCollectionToFile(context, currentCollectionName, currentCollection);
        PostmanRestassuredFilter.setCollectionName(null);
    }

    private void saveCollectionToFile(ExtensionContext context, String collectionName, String collectionData) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
        String fileName = collectionName + "_" + timestamp + ".json";
        Path buildDir = Paths.get(context.getConfigurationParameter("buildDir").orElse("build"));
        Path generatedCollectionsDir = buildDir.resolve(GENERATED_COLLECTIONS_DIR);
        String filePath = generatedCollectionsDir.resolve(fileName).toString();

        try {
            Files.createDirectories(generatedCollectionsDir);
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(collectionData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        PostmanRestassuredFilter.removeFolder();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        PostmanRestassuredFilter.setCollectionName(context.getRequiredTestClass().getName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        PostmanRestassuredFilter.addFolder(context.getRequiredTestMethod().getName());
    }
}
