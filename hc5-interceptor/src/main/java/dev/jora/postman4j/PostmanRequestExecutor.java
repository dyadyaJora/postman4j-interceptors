package dev.jora.postman4j;

import dev.jora.postman4j.models.Body;
import dev.jora.postman4j.models.HeaderElement;
import dev.jora.postman4j.models.HeaderUnion;
import dev.jora.postman4j.models.Headers;
import dev.jora.postman4j.models.Information;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.Mode;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.models.RequestClass;
import dev.jora.postman4j.models.RequestUnion;
import dev.jora.postman4j.models.Response;
import dev.jora.postman4j.models.URL;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanSettings;
import dev.jora.postman4j.utils.RequestResponseMode;
import dev.jora.postman4j.utils.SchemaVersion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author dyadyaJora on 11.12.2024
 */
@Slf4j
public class PostmanRequestExecutor extends HttpRequestExecutor {

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

        String collectionName = currentCollectionNameHolder.get() != null ? currentCollectionNameHolder.get() : this.settings.getBaseCollectionName();
        PostmanCollection postmanCollection = data.computeIfAbsent(collectionName, name -> createPostmanCollection(name, this.settings.getSchemaVersion()));
        Items folder = getOrCreateFolder(postmanCollection);
        Items singleItem;
        try {
            singleItem = createSingleItem(request);
        }  catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Response postmanResponse;

        boolean saveToCollection = this.settings.shouldSaveAll() && this.settings.getCustomStatusFilter() == null;
        try {
            if (settings.getSelectedHeaders().stream().anyMatch(headerName -> request.getHeaders(headerName) != null)) {
                saveToCollection = true;
            }
            ClassicHttpResponse response = super.execute(request, conn, context);

            if (settings.getCustomStatusFilter() != null) {
                saveToCollection = settings.getCustomStatusFilter().test(response.getCode());
            }

            if (settings.getRequestResponseMode() == RequestResponseMode.REQUEST_AND_RESPONSE) {
                postmanResponse = processResponse(response, singleItem, singleItem.getRequest());
                String postmanResponseName = currentResponseNameHolder.get() == null ? response.getCode() + " " + response.getReasonPhrase() : currentResponseNameHolder.get();
                postmanResponse.setName(postmanResponseName);
            }
            return response;
        } catch (IOException | HttpException | RuntimeException e) {
            if (settings.getSelectedExceptions().stream().anyMatch(className -> StringUtils.containsIgnoreCase(e.getClass().getName(), className))) {
                saveToCollection = true;
            }
            throw e;
        } finally {
            if (saveToCollection) {
                if (folder != null) {
                    folder.getItem().add(singleItem);
                } else {
                    postmanCollection.getItem().add(singleItem);
                }
            }
            log.debug("Request executed");
            log.debug(ConverterUtils.toJsonString(postmanCollection));
        }
    }

    private String generateRequestName(ClassicHttpRequest request) {
        if (currentRequestNameHolder.get() != null) {
            return currentRequestNameHolder.get();
        }
        switch (this.settings.getItemNamingStrategy()) {
            case COUNTER:
                return "Request " + this.counter.incrementAndGet();
            case UUID:
                return java.util.UUID.randomUUID().toString();
            case FROM_HEADER:
                if (this.settings.getHeaderName() != null) {
                    return Stream.of(request.getHeaders("Name"))
                            .filter(Objects::nonNull)
                            .findFirst().map(Header::getValue).orElse("Unnamed request");
                }
            default:
                return "Unnamed request";
        }
    }

    private Body fillRequestBody(ClassicHttpRequest request) throws IOException, ParseException {
        Body body = new Body();
        body.setDisabled(true);
        if (this.settings.isEnableRequestBody() && request.getEntity() != null) {
            body.setDisabled(false);
            // @TODO: add support for all types of bodies by header
            body.setMode(Mode.RAW);
            body.setRaw(EntityUtils.toString(request.getEntity()));
        }
        return body;
    }

    private PostmanCollection createPostmanCollection(String name, SchemaVersion schemaVersion) {
        PostmanCollection postmanCollection = new PostmanCollection();
        Information information = new Information();
        information.setName(name);
        information.setSchema(schemaVersion.getSchemaUrl());
        postmanCollection.setInfo(information);
        postmanCollection.setItem(new ArrayList<>());
        return postmanCollection;
    }

    private Items createSingleItem(ClassicHttpRequest request) throws URISyntaxException, IOException, ParseException {
        Items singleItem = new Items();
        singleItem.setName(generateRequestName(request));
        singleItem.setId(UUID.randomUUID().toString());

        RequestUnion requestUnion = new RequestUnion();
        RequestClass requestClass = new RequestClass();
        requestClass.setMethod(request.getMethod());

        URL requestURL = new URL();
        requestURL.setStringValue(request.getUri().toString());
        requestClass.setUrl(requestURL);
        requestClass.setBody(fillRequestBody(request));
        requestUnion.setRequestClassValue(requestClass);

        HeaderUnion headerUnion = new HeaderUnion();
        List<dev.jora.postman4j.models.Header> headers = new ArrayList<>();
        headerUnion.setHeaderArrayValue(headers);

        for (org.apache.hc.core5.http.Header header : request.getHeaders()) {
            dev.jora.postman4j.models.Header postmanHeader = new dev.jora.postman4j.models.Header();
            postmanHeader.setKey(header.getName());
            postmanHeader.setValue(header.getValue());
            headers.add(postmanHeader);
        }
        requestClass.setHeader(headerUnion);

        singleItem.setRequest(requestUnion);
        return singleItem;
    }

    private Response processResponse(ClassicHttpResponse response, Items singleItem, RequestUnion requestUnion) throws IOException, ParseException {
        List<Response> responses = new ArrayList<>();
        singleItem.setResponse(responses);
        Response postmanResponse = new Response();
        postmanResponse.setCode((long) response.getCode());
        postmanResponse.setStatus(response.getReasonPhrase());
        postmanResponse.setOriginalRequest(requestUnion);
        if (this.settings.isEnableResponseBody() && response.getEntity() != null) {
            HttpEntity originalEntity = response.getEntity();
            if (originalEntity != null) {
                byte[] bodyContent = IOUtils.toByteArray(originalEntity.getContent());
                postmanResponse.setBody(IOUtils.toString(bodyContent, "UTF-8"));
                response.setEntity(new CachedHttpEntity(bodyContent));
            }
        }


        Headers responseHeaders = new Headers();
        List<dev.jora.postman4j.models.HeaderElement> responseHeadersList = new ArrayList<>();
        responseHeaders.setUnionArrayValue(responseHeadersList);

        for (Header header : response.getHeaders()) {
            HeaderElement headerElement = new HeaderElement();
            headerElement.headerValue = new dev.jora.postman4j.models.Header();
            headerElement.headerValue.setKey(header.getName());
            headerElement.headerValue.setValue(header.getValue());
            responseHeadersList.add(headerElement);
        }
        postmanResponse.setHeader(responseHeaders);
        responses.add(postmanResponse);
        return postmanResponse;
    }

    static String trimAndRemoveSlashes(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private Items getOrCreateFolder(PostmanCollection postmanCollection) {
        List<String> folderNames;
        if (currentFolderPathHolder.get() != null) {
            folderNames = List.of(currentFolderPathHolder.get().split("/"));
        } else {
            folderNames = currentFoldersHolder.get();
        }
        return getOrCreateFolder(postmanCollection, folderNames);
    }

    private Items getOrCreateFolder(PostmanCollection postmanCollection, List<String> folderNames) {
        if (folderNames == null) {
            return null;
        }

        Items folder = null;
        if (postmanCollection.getItem() == null) {
            postmanCollection.setItem(new ArrayList<>());
        }
        Optional<List<Items>> folders = Optional.ofNullable(postmanCollection.getItem());
        for (String folderName : folderNames) {
            Optional<List<Items>> finalFolders = folders;
            folder = folders.flatMap(f -> f.stream()
                            .filter(item -> item.getName().equals(folderName))
                            .findFirst())
                    .orElseGet(() -> {
                        Items newFolder = new Items();
                        newFolder.setName(folderName);
                        newFolder.setId(UUID.randomUUID().toString());
                        newFolder.setItem(new ArrayList<>());
                        finalFolders.ifPresent(f -> f.add(newFolder));
                        return newFolder;
                    });
            folders = Optional.ofNullable(folder.getItem());
        }
        return folder;
    }
}
