package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * A script is a snippet of Javascript code that can be used to to perform setup or teardown
 * operations on a particular response.
 */
@lombok.Data
public class Script {
    private Host exec;
    private String id;
    private String name;
    private URL src;
    private String type;
}
