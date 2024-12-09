package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * Detailed description of the info block
 */
@lombok.Data
public class Information {
    private String postmanID;
    private DescriptionUnion description;
    private String name;
    private String schema;
    private CollectionVersion version;
}
