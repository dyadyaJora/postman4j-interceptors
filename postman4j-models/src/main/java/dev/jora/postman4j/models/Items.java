package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Items are entities which contain an actual HTTP request, and sample responses attached to
 * it.
 *
 * One of the primary goals of Postman is to organize the development of APIs. To this end,
 * it is necessary to be able to group requests together. This can be achived using
 * 'Folders'. A folder just is an ordered set of requests.
 */
@lombok.Data
public class Items {
    private DescriptionUnion description;
    private List<Event> event;
    private String id;
    private String name;
    private Map<String, Object> protocolProfileBehavior;
    private RequestUnion request;
    private List<Response> response;
    private List<Variable> variable;
    private Auth auth;
    private List<Items> item;
}
