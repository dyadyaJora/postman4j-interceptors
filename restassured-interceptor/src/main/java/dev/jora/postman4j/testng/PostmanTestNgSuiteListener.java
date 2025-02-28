package dev.jora.postman4j.testng;

import dev.jora.postman4j.FilterFactory;
import dev.jora.postman4j.PostmanRestassuredFilter;
import dev.jora.postman4j.utils.ConverterUtils;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.jora.postman4j.PostmanRestassuredUtils.saveCollectionToFile;

/**
 * @author dyadyaJora on 18.02.2025
 */
public class PostmanTestNgSuiteListener implements ISuiteListener {
    private final PostmanRestassuredFilter postmanRestassuredFilter;

    public PostmanTestNgSuiteListener() {
        postmanRestassuredFilter = FilterFactory.getInstance();
        RestAssured.filters(postmanRestassuredFilter);
    }

    @Override
    public void onStart(ISuite iSuite) {
        PostmanRestassuredFilter.setCollectionName(iSuite.getName());
    }

    @SneakyThrows
    @Override
    public void onFinish(ISuite iSuite) {
        String currentCollectionName = PostmanRestassuredFilter.getCollectionName();
        String currentCollection = ConverterUtils.toJsonString(postmanRestassuredFilter.getData().get(currentCollectionName));
        Path buildDir = Paths.get("./build");
        saveCollectionToFile(buildDir, currentCollectionName, currentCollection);
        PostmanRestassuredFilter.setCollectionName(null);
    }
}
