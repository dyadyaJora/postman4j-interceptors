package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@lombok.Data
public class RequestClass {
    private Auth auth;
    private Body body;
    private Certificate certificate;
    private DescriptionUnion description;
    private HeaderUnion header;
    private String method;
    private ProxyConfig proxy;
    private URL url;
}
