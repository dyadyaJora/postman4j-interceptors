package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Map;

@lombok.Data
public class PostmanCollection {
    private Auth auth;
    private List<Event> event;
    private Information info;
    private List<Items> item;
    private Map<String, Object> protocolProfileBehavior;
    private List<Variable> variable;
}
