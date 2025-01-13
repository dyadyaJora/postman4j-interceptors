package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * A Cookie, that follows the [Google Chrome
 * format](https://developer.chrome.com/extensions/cookies)
 */
@lombok.Data
public class Cookie {
    private String domain;
    private String expires;
    private List<Object> extensions;
    private Boolean hostOnly;
    private Boolean httpOnly;
    private String maxAge;
    private String name;
    private String path;
    private Boolean secure;
    private Boolean session;
    private String value;
}
