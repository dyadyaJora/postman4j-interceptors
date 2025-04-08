package dev.jora.postman4j.core;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author dyadyaJora on 06.04.2025
 */
@Data
public class PostmanContextContainer {
    private final AtomicReference<IPostmanContext> clientContext = new AtomicReference<>();
    private final AtomicReference<IPostmanContext> serverContext = new AtomicReference<>();
}
