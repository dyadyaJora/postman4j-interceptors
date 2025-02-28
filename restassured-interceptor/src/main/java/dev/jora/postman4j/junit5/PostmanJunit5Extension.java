package dev.jora.postman4j.junit5;

import dev.jora.postman4j.FilterFactory;
import dev.jora.postman4j.PostmanRestassuredFilter;
import dev.jora.postman4j.utils.ConverterUtils;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.jora.postman4j.PostmanRestassuredUtils.saveCollectionToFile;

/**
 * @author dyadyaJora on 16.02.2025
 */
public class PostmanJunit5Extension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private final PostmanRestassuredFilter postmanRestassuredFilter;
    public PostmanJunit5Extension() {
        postmanRestassuredFilter = FilterFactory.getInstance();
        RestAssured.filters(postmanRestassuredFilter);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String currentCollectionName = postmanRestassuredFilter.getCollectionName();
        String currentCollection = ConverterUtils.toJsonString(postmanRestassuredFilter.getData().get(currentCollectionName));
        Path buildDir = Paths.get(context.getConfigurationParameter("buildDir").orElse("build"));
        saveCollectionToFile(buildDir, currentCollectionName, currentCollection);
        PostmanRestassuredFilter.setCollectionName(null);
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
