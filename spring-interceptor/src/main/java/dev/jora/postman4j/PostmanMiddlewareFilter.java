package dev.jora.postman4j;

import dev.jora.postman4j.models.Header;
import dev.jora.postman4j.models.HeaderElement;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.PostmanSettings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class PostmanMiddlewareFilter extends OncePerRequestFilter implements BasePostmanInterceptor<ContentCachingRequestWrapper, ContentCachingResponseWrapper> {

    private final PostmanSettings settings;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Getter
    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();

    @Getter
    enum Attribute {
        COLLECTION_NAME("dev.jora.postman4j.collection.name"),
        FOLDER_PATH("dev.jora.postman4j.folder.path"),
        REQUEST_NAME("dev.jora.postman4j.request.name"),
        RESPONSE_NAME("dev.jora.postman4j.response.name");

        private final String value;
        Attribute(String value) {
            this.value = value;
        }
    }

    public PostmanMiddlewareFilter() {
        this(PostmanSettings.builder().build());
    }

    public PostmanMiddlewareFilter(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            log.info("Request processed: {}", request.getAttributeNames());
            try {
                if (!(this.getSettings().isDisableOnActuator() && request.getRequestURI().contains("/actuator"))) {
                    this.process(wrappedRequest, () -> wrappedResponse);
                }
            } catch (Exception e) {
                log.error("Could not to save current http call in form of postman collection", e);
            }

            wrappedResponse.copyBodyToResponse();
        }

    }

    @Override
    public AtomicInteger getCounter() {
        return this.counter;
    }

    @Override
    public PostmanSettings getSettings() {
        return this.settings;
    }

    @Override
    public String extractRequestName(ContentCachingRequestWrapper request) {
        return request.getAttribute(Attribute.REQUEST_NAME.getValue()) == null ? null : request.getAttribute(Attribute.REQUEST_NAME.getValue()).toString();
    }

    @Override
    public Optional<String> extractHeaderValue(ContentCachingRequestWrapper request) {
        return Collections.list(request.getHeaders(this.settings.getHeaderName())).stream()
                .filter(Objects::nonNull)
                .findFirst();
    }

    @Override
    public boolean isHeaderExistsInRequest(ContentCachingRequestWrapper request, String headerName) {
        return request.getHeaders(headerName) != null;
    }

    @Override
    public String extractCollectionName(ContentCachingRequestWrapper request) {
        return request.getAttribute(Attribute.COLLECTION_NAME.getValue()) != null ? request.getAttribute(Attribute.COLLECTION_NAME.getValue()).toString() : this.settings.getBaseCollectionName();
    }

    @Override
    public List<String> extractFolderNames(ContentCachingRequestWrapper request) {
        if (request.getAttribute(Attribute.FOLDER_PATH.getValue()) != null) {
            return List.of(request.getAttribute(Attribute.FOLDER_PATH.getValue()).toString().split("/"));
        }
        return new ArrayList<>();
    }

    @Override
    public String extractRequestMethodName(ContentCachingRequestWrapper request) {
        return request.getMethod();
    }

    @Override
    public String extractRequestUrl(ContentCachingRequestWrapper request) {
        return String.valueOf(request.getRequestURL());
    }

    @Override
    public boolean hasRequestBody(ContentCachingRequestWrapper request) {
        if (request.getContentLength() > 0) {
            return true;
        }

        try {
            return request.getInputStream().available() > 0;
        } catch (IOException e) {
            log.error("Could not to check if request has body", e);
            return false;
        }
    }

    @Override
    public String extractRequestBody(ContentCachingRequestWrapper request) {
        return request.getContentAsString();
    }

    @Override
    public List<Header> extractRequestHeaders(ContentCachingRequestWrapper request) {
        List<dev.jora.postman4j.models.Header> headers = new ArrayList<>();
        Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> {
                    String headerValue = request.getHeader(headerName);
                    dev.jora.postman4j.models.Header postmanHeader = new dev.jora.postman4j.models.Header();
                    postmanHeader.setKey(headerName);
                    postmanHeader.setValue(headerValue);
                    return postmanHeader;
                })
                .forEach(headers::add);
        return headers;
    }

    @Override
    public int extractResponseStatusCode(ContentCachingResponseWrapper response) {
        return response.getStatus();
    }

    @Override
    public String extractResponseReasonPhrase(ContentCachingResponseWrapper response) {
        return HttpStatus.valueOf(response.getStatus()).getReasonPhrase();
    }

    @Override
    public boolean hasResponseBody(ContentCachingResponseWrapper response) {
        return response.getContentSize() > 0;
    }

    @Override
    public String extractResponseBody(ContentCachingResponseWrapper response) {
        byte[] bodyContent = response.getContentAsByteArray();
        return IOUtils.toString(bodyContent, "UTF-8");
    }

    @Override
    public List<HeaderElement> extractResponseHeaders(ContentCachingResponseWrapper response) {
        List<dev.jora.postman4j.models.HeaderElement> responseHeadersList = new ArrayList<>();

        for (String headerName : response.getHeaderNames()) {
            HeaderElement headerElement = new HeaderElement();
            headerElement.headerValue = new dev.jora.postman4j.models.Header();
            headerElement.headerValue.setKey(headerName);
            headerElement.headerValue.setValue(response.getHeader(headerName));
            responseHeadersList.add(headerElement);
        }

        return responseHeadersList;
    }

    @Override
    public String extractResponseName(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        return request.getAttribute(Attribute.RESPONSE_NAME.getValue()) == null ? response.getStatus() + " " + HttpStatus.valueOf(response.getStatus()).getReasonPhrase() : request.getAttribute(Attribute.RESPONSE_NAME.getValue()).toString();
    }
}