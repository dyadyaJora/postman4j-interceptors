package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@lombok.Data
public class URLClass {
    private String hash;
    private Host host;
    private URLPath path;
    private String port;
    private String protocol;
    private List<QueryParam> query;
    private String raw;
    private List<Variable> variable;
}
