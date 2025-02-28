package dev.jora.postman4j;

import dev.jora.postman4j.utils.PostmanSettings;

/**
 * @author dyadyaJora on 28.02.2025
 */
public class FilterFactory {

    private static volatile PostmanSettings postmanSettings = new PostmanSettings();
    private static volatile PostmanRestassuredFilter filterInstance;

    private FilterFactory() {
    }

    public static synchronized void setPostmanSettings(PostmanSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("PostmanSettings cannot be null");
        }
        postmanSettings = settings;
        filterInstance = null;
    }

    public static PostmanRestassuredFilter getInstance() {
        if (filterInstance == null) {
            synchronized (FilterFactory.class) {
                if (filterInstance == null) {
                    filterInstance = new PostmanRestassuredFilter(postmanSettings);
                }
            }
        }
        return filterInstance;
    }
}

