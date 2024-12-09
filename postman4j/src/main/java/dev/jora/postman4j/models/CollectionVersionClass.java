package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class CollectionVersionClass {
    private String identifier;
    private long major;
    private Object meta;
    private long minor;
    private long patch;
}
