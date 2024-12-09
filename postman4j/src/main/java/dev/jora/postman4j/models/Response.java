package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * A response represents an HTTP response.
 */
@lombok.Data
public class Response {
    private String body;
    private Long code;
    private List<Cookie> cookie;
    private Headers header;
    private String id;
    private String name;
    private RequestUnion originalRequest;
    private ResponseTime responseTime;
    private String status;
    private Map<String, Object> timings;
}
