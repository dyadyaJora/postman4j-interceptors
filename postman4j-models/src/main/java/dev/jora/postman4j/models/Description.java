package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class Description {
    private String content;
    private String type;
    private Object version;
}
