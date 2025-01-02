package dev.jora.postman4j;

import dev.jora.postman4j.models.HeaderElement;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.PostmanSettings;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


/**
 * @author dyadyaJora on 11.12.2024
 */
public class PostmanRequestExecutor extends HttpRequestExecutor implements BasePostmanInterceptor<ClassicHttpRequest, ClassicHttpResponse> {

    private final PostmanSettings settings;
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final ThreadLocal<String> currentCollectionNameHolder = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> currentFoldersHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> currentFolderPathHolder = new ThreadLocal<>();

    private static final ThreadLocal<String> currentRequestNameHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> currentResponseNameHolder = new ThreadLocal<>();

    @Getter
    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();


    public PostmanRequestExecutor() {
        this(PostmanSettings.builder().build());
    }

    public PostmanRequestExecutor(PostmanSettings settings) {
        this.settings = settings;
    }

    public static void setCollectionName(String prefix) {
        currentCollectionNameHolder.set(prefix);
    }

    public static void removeCollectionName() {
        currentCollectionNameHolder.remove();
    }

    public static void setRequestName(String prefix) {
        currentRequestNameHolder.set(prefix);
    }

    public static void removeRequestName() {
        currentRequestNameHolder.remove();
    }

    public static void setResponseName(String prefix) {
        currentResponseNameHolder.set(prefix);
    }

    public static void removeResponseName() {
        currentResponseNameHolder.remove();
    }

    public static void setFolderPath(String prefix) {
        currentFolderPathHolder.set(prefix);
    }

    public static void removeFolderPath() {
        currentFolderPathHolder.remove();
    }

    public static void addFolder(String folder) {
        List<String> folders = currentFoldersHolder.get();
        if (folders == null) {
            folders = new ArrayList<>();
            currentFoldersHolder.set(folders);
        }
        folders.add(folder);
    }

    public static void removeFolder() {
        List<String> folders = currentFoldersHolder.get();
        if (folders != null) {
            if (!folders.isEmpty()) {
                folders.remove(folders.size() - 1);
            }
        }
    }

    @Override
    public ClassicHttpResponse execute(
            final ClassicHttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context) throws IOException, HttpException {
        try {
            return this.process(request, () -> super.execute(request, conn, context));
        } catch (IOException | HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> extractHeaderValue(ClassicHttpRequest request) {
        return Stream.of(request.getHeaders(this.settings.getHeaderName()))
                .filter(Objects::nonNull)
                .findFirst().map(Header::getValue);
    }

    @Override
    public boolean isHeaderExistsInRequest(ClassicHttpRequest request, String headerName) {
        return request.getHeaders(headerName) != null;
    }

    @Override
    public String extractCollectionName(ClassicHttpRequest request) {
        return currentCollectionNameHolder.get() != null ? currentCollectionNameHolder.get() : this.settings.getBaseCollectionName();
    }

    @Override
    public List<String> extractFolderNames(ClassicHttpRequest request) {
        if (currentFolderPathHolder.get() != null) {
            return List.of(currentFolderPathHolder.get().split("/"));
        }
        return currentFoldersHolder.get();
    }

    @Override
    public String extractRequestMethodName(ClassicHttpRequest request) {
        return request.getMethod();
    }

    @Override
    public String extractRequestUrl(ClassicHttpRequest request) {
        try {
            return request.getUri().toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public boolean hasRequestBody(ClassicHttpRequest request) {
        return request.getEntity() != null;
    }

    @Override
    public String extractRequestBody(ClassicHttpRequest request) {
        try {
            return EntityUtils.toString(request.getEntity());
        } catch (ParseException | IOException e) {
            return null;
        }
    }

    @Override
    public List<dev.jora.postman4j.models.Header> extractRequestHeaders(ClassicHttpRequest request) {
        List<dev.jora.postman4j.models.Header> headers = new ArrayList<>();
        for (org.apache.hc.core5.http.Header header : request.getHeaders()) {
            dev.jora.postman4j.models.Header postmanHeader = new dev.jora.postman4j.models.Header();
            postmanHeader.setKey(header.getName());
            postmanHeader.setValue(header.getValue());
            headers.add(postmanHeader);
        }
        return headers;
    }

    @Override
    public int extractResponseStatusCode(ClassicHttpResponse response) {
        return response.getCode();
    }

    @Override
    public String extractResponseReasonPhrase(ClassicHttpResponse response) {
        return response.getReasonPhrase();
    }

    @Override
    public boolean hasResponseBody(ClassicHttpResponse response) {
        return response.getEntity() != null;
    }

    @Override
    public String extractResponseBody(ClassicHttpResponse response) {
        HttpEntity originalEntity = response.getEntity();
        if (originalEntity == null) {
            return null;
        }
        try {
            byte[] bodyContent = IOUtils.toByteArray(originalEntity.getContent());
            response.setEntity(new CachedHttpEntity(bodyContent));
            return IOUtils.toString(bodyContent, "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public List<HeaderElement> extractResponseHeaders(ClassicHttpResponse response) {
        List<dev.jora.postman4j.models.HeaderElement> responseHeadersList = new ArrayList<>();
        for (Header header : response.getHeaders()) {
            HeaderElement headerElement = new HeaderElement();
            headerElement.headerValue = new dev.jora.postman4j.models.Header();
            headerElement.headerValue.setKey(header.getName());
            headerElement.headerValue.setValue(header.getValue());
            responseHeadersList.add(headerElement);
        }

        return responseHeadersList;
    }

    @Override
    public String extractResponseName(ClassicHttpRequest request, ClassicHttpResponse response) {
        return currentResponseNameHolder.get() == null ? response.getCode() + " " + response.getReasonPhrase() : currentResponseNameHolder.get();
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
    public String extractRequestName(ClassicHttpRequest request) {
        return currentRequestNameHolder.get();
    }
}
