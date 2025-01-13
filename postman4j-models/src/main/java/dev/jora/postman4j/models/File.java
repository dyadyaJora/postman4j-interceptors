package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
public class File {
    private String content;
    private String src;
}
