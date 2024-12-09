package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@lombok.Data
public class FormParameter {
    private String contentType;
    private DescriptionUnion description;
    private Boolean disabled;
    private String key;
    private FormParameterType type;
    private String value;
    private Src src;
}
