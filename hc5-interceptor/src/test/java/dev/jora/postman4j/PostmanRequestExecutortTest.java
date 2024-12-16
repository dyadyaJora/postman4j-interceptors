package dev.jora.postman4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author dyadyaJora on 16.12.2024
 */
public class PostmanRequestExecutortTest {
    @Test
    public void testSlashes() {
        assertEquals("/", PostmanRequestExecutor.trimAndRemoveSlashes("///"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("/aa/bb/"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("aa/bb/"));
        assertEquals("aa/bb", PostmanRequestExecutor.trimAndRemoveSlashes("/aa/bb"));
    }
}
