package dev.jora.postman4j;

import dev.jora.postman4j.models.Header;
import dev.jora.postman4j.models.HeaderElement;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.PostmanSettings;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author dyadyaJora on 31.01.2025
 */
@Getter
public class PostmanRestassuredFilter implements Filter, BasePostmanInterceptor<FilterableRequestSpecification, Response> {

    private final PostmanSettings settings;

    private final AtomicInteger counter = new AtomicInteger(0);

    private static final ThreadLocal<String> currentCollectionNameHolder = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> currentFoldersHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> currentFolderPathHolder = new ThreadLocal<>();

    private static final ThreadLocal<String> currentRequestNameHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> currentResponseNameHolder = new ThreadLocal<>();
    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();


    public PostmanRestassuredFilter() {
        this(PostmanSettings.builder().build());
    }

    public PostmanRestassuredFilter(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {

        try {
            return this.process(requestSpec, () -> ctx.next(requestSpec, responseSpec));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String extractRequestName(FilterableRequestSpecification request) {
        return currentRequestNameHolder.get();
    }

    @Override
    public Optional<String> extractHeaderValue(FilterableRequestSpecification request) {
        return Stream.of(request.getHeaders().getValues(this.settings.getHeaderName()))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .findFirst();
    }

    @Override
    public boolean isHeaderExistsInRequest(FilterableRequestSpecification request, String headerName) {
        return request.getHeaders().getValue(headerName) != null;
    }

    @Override
    public String extractCollectionName(FilterableRequestSpecification request) {
        return currentCollectionNameHolder.get() != null ? currentCollectionNameHolder.get() : this.settings.getBaseCollectionName();
    }

    @Override
    public List<String> extractFolderNames(FilterableRequestSpecification request) {
        if (currentFolderPathHolder.get() != null) {
            return List.of(currentFolderPathHolder.get().split("/"));
        }
        return currentFoldersHolder.get();
    }

    @Override
    public String extractRequestMethodName(FilterableRequestSpecification request) {
        return request.getMethod();
    }

    @Override
    public String extractRequestUrl(FilterableRequestSpecification request) {
        return request.getURI();
    }

    @Override
    public boolean hasRequestBody(FilterableRequestSpecification request) {
        return request.getBody() != null;
    }

    @Override
    public String extractRequestBody(FilterableRequestSpecification request) {
        return new Prettifier().getPrettifiedBodyIfPossible(request);
    }

    @Override
    public List<Header> extractRequestHeaders(FilterableRequestSpecification request) {
        List<dev.jora.postman4j.models.Header> headers = new ArrayList<>();
        for (io.restassured.http.Header header : request.getHeaders().asList()) {
            dev.jora.postman4j.models.Header postmanHeader = new dev.jora.postman4j.models.Header();
            postmanHeader.setKey(header.getName());
            postmanHeader.setValue(header.getValue());
            headers.add(postmanHeader);
        }
        return headers;
    }

    @Override
    public int extractResponseStatusCode(Response response) {
        return response.getStatusCode();
    }

    @Override
    public String extractResponseReasonPhrase(Response response) {
        // @TODO: implement
        return "";
    }

    @Override
    public boolean hasResponseBody(Response response) {
        return response.getBody() != null;
    }

    @Override
    public String extractResponseBody(Response response) {
        return response.getBody().prettyPrint();
    }

    @Override
    public List<HeaderElement> extractResponseHeaders(Response response) {
        List<dev.jora.postman4j.models.HeaderElement> responseHeadersList = new ArrayList<>();
        for (io.restassured.http.Header header : response.getHeaders().asList()) {
            HeaderElement headerElement = new HeaderElement();
            headerElement.headerValue = new dev.jora.postman4j.models.Header();
            headerElement.headerValue.setKey(header.getName());
            headerElement.headerValue.setValue(header.getValue());
            responseHeadersList.add(headerElement);
        }

        return responseHeadersList;
    }

    @Override
    public String extractResponseName(FilterableRequestSpecification request, Response response) {
        return currentResponseNameHolder.get() == null ? response.getStatusLine() : currentResponseNameHolder.get();
    }
}

