package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * Postman allows you to configure scripts to run when specific events occur. These scripts
 * are stored here, and can be referenced in the collection by their ID.
 *
 * Defines a script associated with an associated event name
 */
@lombok.Data
public class Event {
    private Boolean disabled;
    private String id;
    private String listen;
    private Script script;
}
