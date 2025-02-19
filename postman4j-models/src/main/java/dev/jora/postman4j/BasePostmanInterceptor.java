package dev.jora.postman4j;

import dev.jora.postman4j.models.Body;
import dev.jora.postman4j.models.FormParameter;
import dev.jora.postman4j.models.HeaderUnion;
import dev.jora.postman4j.models.Headers;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.Mode;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.models.RequestClass;
import dev.jora.postman4j.models.RequestUnion;
import dev.jora.postman4j.models.Response;
import dev.jora.postman4j.models.URL;
import dev.jora.postman4j.utils.ConverterUtils;
import dev.jora.postman4j.utils.PostmanCollectionFactory;
import dev.jora.postman4j.utils.PostmanSettings;
import dev.jora.postman4j.utils.RequestResponseMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.jora.postman4j.utils.PostmanCollectionFactory.createPostmanCollection;

/**
 * @author dyadyaJora on 02.01.2025
 */
public interface BasePostmanInterceptor<Req, Resp> {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggerFactory.getLogger(BasePostmanInterceptor.class).getClass());

    ConcurrentHashMap<String, PostmanCollection> getData();

    AtomicInteger getCounter();

    PostmanSettings getSettings();

    String extractRequestName(Req request);

    Optional<String> extractHeaderValue(Req request);

    boolean isHeaderExistsInRequest(Req request, String headerName);

    String extractCollectionName(Req request);

    List<String> extractFolderNames(Req request);

    String extractRequestMethodName(Req request);

    String extractRequestUrl(Req request);

    boolean hasRequestBody(Req request);
    boolean hasRequestFormData(Req request);

    String extractRequestBody(Req request);
    List<FormParameter> extractRequestFormData(Req request);

    List<dev.jora.postman4j.models.Header> extractRequestHeaders(Req request);

    int extractResponseStatusCode(Resp response);

    String extractResponseReasonPhrase(Resp response);

    boolean hasResponseBody(Resp response);

    String extractResponseBody(Resp response);

    List<dev.jora.postman4j.models.HeaderElement> extractResponseHeaders(Resp response);

    String extractResponseName(Req request, Resp response);

    default String generateRequestName(Req request) {
        if (this.extractRequestName(request) != null) {
            return this.extractRequestName(request);
        }
        switch (this.getSettings().getItemNamingStrategy()) {
            case COUNTER:
                return "Request " + this.getCounter().incrementAndGet();
            case UUID:
                return java.util.UUID.randomUUID().toString();
            case FROM_HEADER:
                if (this.getSettings().getHeaderName() != null) {
                    return extractHeaderValue(request).orElse("Unnamed request");
                }
            default:
                return "Unnamed request";
        }
    }

    default Resp process(Req request, Callable<Resp> callResponse) throws Exception {
        return process(request, callResponse, null);
    }

    default Resp process(Req request, Callable<Resp> callResponse, Exception throwedException) throws Exception {
        String collectionName = extractCollectionName(request);
        PostmanCollection postmanCollection = this.getData().computeIfAbsent(collectionName, name -> createPostmanCollection(name, this.getSettings().getSchemaVersion()));
        Items folder = PostmanCollectionFactory.getOrCreateFolder(postmanCollection, this.extractFolderNames(request));
        Items singleItem;
        try {
            singleItem = createSingleItem(request);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
        Response postmanResponse;

        boolean saveToCollection = this.getSettings().shouldSaveAll() && this.getSettings().getCustomStatusFilter() == null;
        try {
            Resp response = callResponse.call();
            if (this.getSettings().getSelectedHeaders().stream().anyMatch(headerName -> this.isHeaderExistsInRequest(request, headerName))) {
                saveToCollection = true;
            }

            if (this.getSettings().getCustomStatusFilter() != null) {
                saveToCollection = this.getSettings().getCustomStatusFilter().test(this.extractResponseStatusCode(response));
            }

            if (this.getSettings().getRequestResponseMode() == RequestResponseMode.REQUEST_AND_RESPONSE) {
                if (throwedException == null) {
                    postmanResponse = processResponse(response, singleItem, singleItem.getRequest());
                    String postmanResponseName = this.extractResponseName(request, response);
                    postmanResponse.setName(postmanResponseName);
                } else {
                    List<Response> responses = new ArrayList<>();
                    postmanResponse = new Response();
                    postmanResponse.setName(throwedException.getClass().getName());
                    responses.add(postmanResponse);
                    singleItem.setResponse(responses);
                    throw throwedException;
                }
            }
            return response;
        } catch (Exception e) {
            if (this.getSettings().getSelectedExceptions().stream().anyMatch(className -> StringUtils.containsIgnoreCase(e.getClass().getName(), className))) {
                saveToCollection = true;
            }
            throw e;
        } finally {
            log.debug("Request executed.");
            if (saveToCollection) {
                log.debug("Collection saved.");
                if (folder != null) {
                    folder.getItem().add(singleItem);
                } else {
                    postmanCollection.getItem().add(singleItem);
                }
                if (this.getSettings().isLogWhenChanged()) {
                    log.info(ConverterUtils.toJsonString(postmanCollection));
                }
            }
        }
    }

    private Items createSingleItem(Req request) throws IOException {
        Items singleItem = new Items();
        singleItem.setName(generateRequestName(request));
        singleItem.setId(UUID.randomUUID().toString());

        RequestUnion requestUnion = new RequestUnion();
        RequestClass requestClass = new RequestClass();
        requestClass.setMethod(this.extractRequestMethodName(request));

        URL requestURL = new URL();
        requestURL.setStringValue(this.extractRequestUrl(request));
        requestClass.setUrl(requestURL);
        requestClass.setBody(fillRequestBody(request));
        requestUnion.setRequestClassValue(requestClass);

        HeaderUnion headerUnion = new HeaderUnion();
        List<dev.jora.postman4j.models.Header> headers = this.extractRequestHeaders(request);
        headerUnion.setHeaderArrayValue(headers);
        requestClass.setHeader(headerUnion);

        singleItem.setRequest(requestUnion);
        return singleItem;
    }

    private Body fillRequestBody(Req request) {
        Body body = new Body();
        body.setDisabled(true);
        if (this.getSettings().isEnableRequestBody()) {
            if (hasRequestBody(request)) {
                body.setDisabled(false);
                // @TODO: add support for all types of bodies by header
                body.setMode(Mode.RAW);
                body.setRaw(this.extractRequestBody(request));
            }
            if (hasRequestFormData(request)) {
                body.setDisabled(false);
                body.setMode(Mode.FORMDATA);
                body.setFormdata(this.extractRequestFormData(request));
            }
        }
        return body;
    }

    private Response processResponse(Resp response, Items singleItem, RequestUnion requestUnion) throws IOException {
        List<Response> responses = new ArrayList<>();
        singleItem.setResponse(responses);
        Response postmanResponse = new Response();
        postmanResponse.setCode((long) this.extractResponseStatusCode(response));
        postmanResponse.setStatus(this.extractResponseReasonPhrase(response));
        postmanResponse.setOriginalRequest(requestUnion);
        if (this.getSettings().isEnableResponseBody() && this.hasResponseBody(response)) {
            postmanResponse.setBody(this.extractResponseBody(response));
        }

        Headers responseHeaders = new Headers();
        List<dev.jora.postman4j.models.HeaderElement> responseHeadersList = this.extractResponseHeaders(response);
        responseHeaders.setUnionArrayValue(responseHeadersList);
        postmanResponse.setHeader(responseHeaders);
        responses.add(postmanResponse);
        return postmanResponse;
    }

}
