package dev.jora.postman4j.core;

import dev.jora.postman4j.utils.PostmanSettings;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Common interface for interceptors context
 */
public interface IPostmanContext {
    String DEFAULT_CONTEXT_ID = "defaultContextId";

    /**
     * Returns unique identifier for this interceptor context
     */
    String getContextId();

    /**
     * Returns the Postman context settings for this interceptor
     * @return The Postman context settings
     */
    PostmanSettings getSettings();

    /**
     * Update ostman context settings for this interceptor
     */
    void setSettings(PostmanSettings settings);

    /**
     * Returns the request counter for this interceptor context
     * @return The request counter
     */
    AtomicInteger getCounter();

    /**
     * Sets the current Postman collection name in the context
     * @param name The collection name to set
     */
    void setCollectionName(String name);

    /**
     * Returns the current Postman collection name from the context
     * @return The collection name
     */
    String getCollectionName();

    /**
     * Removes the current Postman collection name from the context
     */
    void removeCollectionName();

    /**
     * Sets the current Postman folder path in the context
     * @param path The folder path to set
     */
    void setFolderPath(String path);

    /**
     * Returns the current Postman folder path from the context
     * @return The folder path
     */
    String getFolderPath();

    /**
     * Removes the current Postman folder path from the context
     */
    void removeFolderPath();

    /**
     * Sets the current Postman request name in the context
     * @param name The request name to set
     */
    void setRequestName(String name);

    /**
     * Returns the current Postman request name from the context
     * @return The request name
     */
    String getRequestName();

    /**
     * Removes the current Postman request name from the context
     */
    void removeRequestName();

    /**
     * Sets the current Postman response name in the context
     * @param name The response name to set
     */
    void setResponseName(String name);

    /**
     * Returns the current Postman response name from the context
     * @return The response name
     */
    String getResponseName();

    /**
     * Removes the current Postman response name from the context
     */
    void removeResponseName();

    /**
     * Adds a folder name to the current context
     * @param folderName The folder name to add
     */
    void addFolder(String folderName);

    /**
     * Returns the current folder names from the context
     * @return The folder names
     */
    List<String> getFolders();

    /**
     * Removes the current folder from the context
     */
    void removeFolder();
}