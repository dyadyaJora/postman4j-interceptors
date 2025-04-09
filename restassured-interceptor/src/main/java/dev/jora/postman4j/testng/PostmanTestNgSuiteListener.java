package dev.jora.postman4j.testng;

import dev.jora.postman4j.FilterFactory;
import dev.jora.postman4j.PostmanRestassuredFilter;
import dev.jora.postman4j.core.IPostmanContext;
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
        String postmanContextId = System.getProperty("postmanContextId", IPostmanContext.DEFAULT_CONTEXT_ID);
        postmanRestassuredFilter = FilterFactory.getInstance();
        RestAssured.filters(postmanRestassuredFilter);
    }

    @Override
    public void onStart(ISuite iSuite) {
        postmanRestassuredFilter.getContext().setCollectionName(iSuite.getName());
    }

    @SneakyThrows
    @Override
    public void onFinish(ISuite iSuite) {
        String currentCollectionName = postmanRestassuredFilter.getContext().getCollectionName();
        String currentCollection = ConverterUtils.toJsonString(postmanRestassuredFilter.getData().get(currentCollectionName));
        Path buildDir = Paths.get("./build");
        saveCollectionToFile(buildDir, currentCollectionName, currentCollection);
        postmanRestassuredFilter.getContext().setCollectionName(null);
    }
}
