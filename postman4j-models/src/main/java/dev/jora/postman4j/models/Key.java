package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;

/**
 * An object containing path to file containing private key, on the file system
 */
@lombok.Data
public class Key {
    private Object src;
}
