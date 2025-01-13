package dev.jora.postman4j.utils;

import dev.jora.postman4j.models.Information;
import dev.jora.postman4j.models.Items;
import dev.jora.postman4j.models.PostmanCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author dyadyaJora on 31.12.2024
 */
public class PostmanCollectionFactory {

    public static Items getOrCreateFolder(PostmanCollection postmanCollection, List<String> folderNames) {
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


    public static PostmanCollection createPostmanCollection(String name, SchemaVersion schemaVersion) {
        PostmanCollection postmanCollection = new PostmanCollection();
        Information information = new Information();
        information.setName(name);
        information.setSchema(schemaVersion.getSchemaUrl());
        postmanCollection.setInfo(information);
        postmanCollection.setItem(new ArrayList<>());
        return postmanCollection;
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
}
