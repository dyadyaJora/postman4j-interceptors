package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * A representation of an ssl certificate
 */
@lombok.Data
public class Certificate {
    private CERT cert;
    private Key key;
    private List<String> matches;
    private String name;
    private String passphrase;
}
