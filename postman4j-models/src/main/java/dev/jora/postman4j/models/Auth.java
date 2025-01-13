package dev.jora.postman4j.models;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * Represents authentication helpers provided by Postman
 */
@lombok.Data
public class Auth {
    private List<ApikeyElement> apikey;
    private List<ApikeyElement> awsv4;
    private List<ApikeyElement> basic;
    private List<ApikeyElement> bearer;
    private List<ApikeyElement> digest;
    private List<ApikeyElement> edgegrid;
    private List<ApikeyElement> hawk;
    private Object noauth;
    private List<ApikeyElement> ntlm;
    private List<ApikeyElement> oauth1;
    private List<ApikeyElement> oauth2;
    private AuthType type;
}
