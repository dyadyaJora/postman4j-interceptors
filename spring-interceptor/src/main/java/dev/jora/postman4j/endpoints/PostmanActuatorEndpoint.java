package dev.jora.postman4j.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.jora.postman4j.PostmanMiddlewareFilter;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.ConverterUtils;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.Set;

/**
 * @author dyadyaJora on 02.01.2025
 */
@Endpoint(id = "postman-collections")
public class PostmanActuatorEndpoint {

    private final PostmanMiddlewareFilter postmanMiddlewareFilter;

    public PostmanActuatorEndpoint(PostmanMiddlewareFilter postmanMiddlewareFilter) {
        this.postmanMiddlewareFilter = postmanMiddlewareFilter;
    }
    @ReadOperation
    public Set<String> listPostmanCollections() {
        if (postmanMiddlewareFilter == null) {
            return Set.of();
        }
        return postmanMiddlewareFilter.getData().keySet();
    }

    @ReadOperation
    public String getPostmanCollection(@Selector String name) throws JsonProcessingException {
        if (postmanMiddlewareFilter == null) {
            return "{}";
        }
        return ConverterUtils.toJsonString(postmanMiddlewareFilter.getData().get(name));
    }

    @WriteOperation
    public void configurePostmanCollection(@Selector String name, PostmanCollection postmanCollection) {
        if (postmanMiddlewareFilter == null) {
            return;
        }
        postmanMiddlewareFilter.getData().put(name, postmanCollection);
    }

    @DeleteOperation
    public void deletePostmanCollection(@Selector String name) {
        if (postmanMiddlewareFilter == null) {
            return;
        }
        postmanMiddlewareFilter.getData().remove(name);
    }
}
