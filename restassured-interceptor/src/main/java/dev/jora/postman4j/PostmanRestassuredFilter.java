package dev.jora.postman4j;

import dev.jora.postman4j.core.IPostmanContext;
import dev.jora.postman4j.core.PostmanContextHolder;
import dev.jora.postman4j.models.FormParameter;
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
import java.util.Map;
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
    private IPostmanContext context;

    public PostmanRestassuredFilter() {
        this(PostmanSettings.builder().build());
    }

    public PostmanRestassuredFilter(PostmanSettings settings) {
        this(null, settings);
    }

    public PostmanRestassuredFilter(String contextId, PostmanSettings settings) {
        this.context = PostmanContextHolder.getInstance().getClientContext(contextId, settings);
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
    public PostmanSettings getSettings() {
        return this.context.getSettings();
    }

    @Override
    public ConcurrentHashMap<String, PostmanCollection> getData() {
        return this.context.getData();
    }

    @Override
    public AtomicInteger getCounter() {
        return this.context.getCounter();
    }

    @Override
    public String extractRequestName(FilterableRequestSpecification request) {
        return this.context.getRequestName();
    }

    @Override
    public Optional<String> extractHeaderValue(FilterableRequestSpecification request) {
        return Stream.of(request.getHeaders().getValues(this.getContext().getSettings().getHeaderName()))
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
        return context.getCollectionName() != null ? context.getCollectionName() : this.context.getSettings().getBaseCollectionName();
    }

    @Override
    public List<String> extractFolderNames(FilterableRequestSpecification request) {
        if (this.context.getFolderPath() != null) {
            return List.of(this.context.getFolderPath().split("/"));
        }
        return this.context.getFolders();
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
    public boolean hasRequestFormData(FilterableRequestSpecification request) {
        return request.getFormParams() != null && !request.getFormParams().isEmpty();
    }

    @Override
    public String extractRequestBody(FilterableRequestSpecification request) {
        return new Prettifier().getPrettifiedBodyIfPossible(request);
    }

    @Override
    public List<FormParameter> extractRequestFormData(FilterableRequestSpecification request) {
        List<FormParameter> formParameters = new ArrayList<>();
        for (Map.Entry<String, String> formParam : request.getFormParams().entrySet()) {
            FormParameter postmanFormParameter = new FormParameter();
            postmanFormParameter.setKey(formParam.getKey());
            postmanFormParameter.setValue(formParam.getValue());
            formParameters.add(postmanFormParameter);
        }
        return formParameters;
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
        return this.context.getResponseName() == null ? response.getStatusLine() : this.context.getResponseName();
    }
}

