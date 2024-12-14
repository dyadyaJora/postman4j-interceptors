package dev.jora.postman4j.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dyadyaJora on 15.12.2024
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UsePostmanRequest {
    String value();
}
