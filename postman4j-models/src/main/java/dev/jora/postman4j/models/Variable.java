package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * Collection variables allow you to define a set of variables, that are a *part of the
 * collection*, as opposed to environments, which are separate entities.
 * *Note: Collection variables must not contain any sensitive information.*
 *
 * Using variables in your Postman requests eliminates the need to duplicate requests, which
 * can save a lot of time. Variables can be defined, and referenced to from any part of a
 * request.
 */
@lombok.Data
public class Variable {
    private DescriptionUnion description;
    private Boolean disabled;
    private String id;
    private String key;
    private String name;
    private Boolean system;
    private VariableType type;
    private Object value;
}
