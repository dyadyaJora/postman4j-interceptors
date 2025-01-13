package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * Represents an attribute for any authorization method provided by Postman. For example
 * `username` and `password` are set as auth attributes for Basic Authentication method.
 */
@lombok.Data
public class ApikeyElement {
    private String key;
    private String type;
    private Object value;
}
