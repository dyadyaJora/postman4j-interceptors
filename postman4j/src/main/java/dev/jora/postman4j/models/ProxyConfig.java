package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * Using the Proxy, you can configure your custom proxy into the postman for particular url
 * match
 */
@lombok.Data
public class ProxyConfig {
    private Boolean disabled;
    private String host;
    private String match;
    private Long port;
    private Boolean tunnel;
}
