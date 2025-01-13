package dev.jora.postman4j.utils;


import lombok.Getter;

@Getter
public enum SchemaVersion {
    V2_1_0("https://schema.getpostman.com/json/collection/v2.1.0/collection.json"),
    V2_0_0("https://schema.getpostman.com/json/collection/v2.0.0/collection.json"),;
    private final String schemaUrl;

    SchemaVersion(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }
}