package dev.jora.postman4j.core;

import dev.jora.postman4j.utils.PostmanSettings;
import lombok.Getter;
import dev.jora.postman4j.models.PostmanCollection;
import java.util.concurrent.ConcurrentHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dyadyaJora on 06.04.2025
 */
@Getter
public class PostmanContext implements IPostmanContext {
    private PostmanSettings settings;
    private final AtomicInteger counter = new AtomicInteger(0);

    private final ThreadLocal<String> currentCollectionNameHolder = new ThreadLocal<>();
    private final ThreadLocal<List<String>> currentFoldersHolder = new ThreadLocal<>();
    private final ThreadLocal<String> currentFolderPathHolder = new ThreadLocal<>();

    private final ThreadLocal<String> currentRequestNameHolder = new ThreadLocal<>();
    private final ThreadLocal<String> currentResponseNameHolder = new ThreadLocal<>();

    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();

    public PostmanContext() {
        this(PostmanSettings.builder().build());
    }
    public PostmanContext(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getContextId() {
        return "";
    }

    @Override
    public void setSettings(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    public void setCollectionName(String name) {
        this.currentCollectionNameHolder.set(name);
    }

    @Override
    public String getCollectionName() {
        return this.currentCollectionNameHolder.get();
    }

    @Override
    public void removeCollectionName() {
        this.currentCollectionNameHolder.remove();
    }

    @Override
    public void setFolderPath(String path) {
        this.currentFolderPathHolder.set(path);
    }

    @Override
    public String getFolderPath() {
        return this.currentFolderPathHolder.get();
    }

    @Override
    public void removeFolderPath() {
        this.currentCollectionNameHolder.remove();
    }

    @Override
    public void setRequestName(String name) {
        this.currentRequestNameHolder.set(name);
    }

    @Override
    public String getRequestName() {
        return this.currentRequestNameHolder.get();
    }

    @Override
    public void removeRequestName() {
        this.currentRequestNameHolder.remove();
    }

    @Override
    public void setResponseName(String name) {
        this.currentResponseNameHolder.set(name);
    }

    @Override
    public String getResponseName() {
        return this.currentResponseNameHolder.get();
    }

    @Override
    public void removeResponseName() {
        this.currentResponseNameHolder.remove();
    }

    @Override
    public void addFolder(String folderName) {
        List<String> folders = this.currentFoldersHolder.get();
        if (folders == null) {
            folders = new ArrayList<>();
            currentFoldersHolder.set(folders);
        }
        folders.add(folderName);
    }

    @Override
    public List<String> getFolders() {
        return this.currentFoldersHolder.get();
    }

    @Override
    public void removeFolder() {
        List<String> folders = this.currentFoldersHolder.get();
        if (folders != null) {
            if (!folders.isEmpty()) {
                folders.remove(folders.size() - 1);
            }
        }
    }

    @Override
    public ConcurrentHashMap<String, PostmanCollection> getData() {
        return this.data;
    }
}
