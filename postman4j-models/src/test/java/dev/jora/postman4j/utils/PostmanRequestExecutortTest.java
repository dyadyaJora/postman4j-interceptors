package dev.jora.postman4j.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author dyadyaJora on 16.12.2024
 */
public class PostmanRequestExecutortTest {
    @Test
    public void testSlashes() {
        assertEquals("/", PostmanCollectionFactory.trimAndRemoveSlashes("///"));
        assertEquals("aa/bb", PostmanCollectionFactory.trimAndRemoveSlashes("/aa/bb/"));
        assertEquals("aa/bb", PostmanCollectionFactory.trimAndRemoveSlashes("aa/bb/"));
        assertEquals("aa/bb", PostmanCollectionFactory.trimAndRemoveSlashes("/aa/bb"));
    }
}
