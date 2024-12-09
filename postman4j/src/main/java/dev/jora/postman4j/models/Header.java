package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * A representation for a list of headers
 *
 * Represents a single HTTP Header
 */
@lombok.Data
public class Header {
    private DescriptionUnion description;
    private Boolean disabled;
    private String key;
    private String value;
}
