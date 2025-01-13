package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class URLEncodedParameter {
    private DescriptionUnion description;
    private Boolean disabled;
    private String key;
    private String value;
}
