package dev.jora.postman4j;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author dyadyaJora on 18.02.2025
 */
public class PostmanRestassuredUtils {
    private static final String GENERATED_COLLECTIONS_DIR = "postman4j-generated";

    public static void saveCollectionToFile(Path buildDir, String collectionName, String collectionData) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
        String fileName = collectionName + "_" + timestamp + ".json";
        Path generatedCollectionsDir = buildDir.resolve(GENERATED_COLLECTIONS_DIR);
        String filePath = generatedCollectionsDir.resolve(fileName).toString();

        try {
            Files.createDirectories(generatedCollectionsDir);
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(collectionData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
