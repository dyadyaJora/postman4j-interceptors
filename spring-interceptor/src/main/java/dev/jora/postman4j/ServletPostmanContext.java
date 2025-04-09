package dev.jora.postman4j;

import dev.jora.postman4j.core.IPostmanContext;
import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.PostmanSettings;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dyadyaJora on 09.04.2025
 */
public class ServletPostmanContext implements IPostmanContext {
    private PostmanSettings settings;
    private final AtomicInteger counter = new AtomicInteger(0);

    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();

    @Override
    public String getContextId() {
        return "";
    }

    @Override
    public PostmanSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setSettings(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    public AtomicInteger getCounter() {
        return this.counter;
    }

    @Override
    public void setCollectionName(String name) {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.COLLECTION_NAME.getValue(), name);
        }
    }

    @Override
    public String getCollectionName() {
        HttpServletRequest request = extractRequestAttributes();
        if (request == null) {
            return null;
        }

        Object attributeValue = request.getAttribute(PostmanMiddlewareFilter.Attribute.COLLECTION_NAME.getValue());
        if (attributeValue == null) {
            return null;
        }

        return attributeValue.toString();
    }

    @Override
    public void removeCollectionName() {
        // do nothing
    }

    @Override
    public void setFolderPath(String path) {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.FOLDER_PATH.getValue(), path);
        }
    }

    @Override
    public String getFolderPath() {
        HttpServletRequest request = extractRequestAttributes();
        if (request == null) {
            return null;
        }

        Object attributeValue = request.getAttribute(PostmanMiddlewareFilter.Attribute.FOLDER_PATH.getValue());
        if (attributeValue == null) {
            return null;
        }

        return attributeValue.toString();
    }

    @Override
    public void removeFolderPath() {
        // do nothing
    }

    @Override
    public void setRequestName(String name) {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.REQUEST_NAME.getValue(), name);
        }
    }

    @Override
    public String getRequestName() {
        HttpServletRequest request = extractRequestAttributes();
        if (request == null) {
            return null;
        }

        Object attributeValue = request.getAttribute(PostmanMiddlewareFilter.Attribute.REQUEST_NAME.getValue());
        if (attributeValue == null) {
            return null;
        }

        return attributeValue.toString();
    }

    @Override
    public void removeRequestName() {
        // do nothing
    }

    @Override
    public void setResponseName(String name) {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.RESPONSE_NAME.getValue(), name);
        }
    }

    @Override
    public String getResponseName() {
        HttpServletRequest request = extractRequestAttributes();
        if (request == null) {
            return null;
        }

        Object attributeValue = request.getAttribute(PostmanMiddlewareFilter.Attribute.RESPONSE_NAME.getValue());
        if (attributeValue == null) {
            return null;
        }

        return attributeValue.toString();
    }

    @Override
    public void removeResponseName() {
        // do nothing
    }

    @Override
    public void addFolder(String folderName) {
        // not supported
    }

    @Override
    public List<String> getFolders() {
        return List.of();
    }

    @Override
    public void removeFolder() {
        // not supported
    }

    @Override
    public ConcurrentHashMap<String, PostmanCollection> getData() {
        return this.data;
    }

    private HttpServletRequest extractRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }
}
