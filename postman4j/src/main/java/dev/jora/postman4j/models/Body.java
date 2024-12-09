package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * This field contains the data usually contained in the request body.
 */
@lombok.Data
public class Body {
    private Boolean disabled;
    private File file;
    private List<FormParameter> formdata;
    private Map<String, Object> graphql;
    private Mode mode;
    private Map<String, Object> options;
    private String raw;
    private List<URLEncodedParameter> urlencoded;
}
