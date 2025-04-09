package dev.jora.postman4j.endpoints;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import dev.jora.postman4j.annotations.UsePostmanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.jora.postman4j.endpoints.SecondController.SECOND_COLLECTION_NAME;


/**
 * @author dyadyaJora on 05.01.2025
 */
//@Slf4j
@RestController
@RequestMapping(produces = "application/json")
@UsePostmanCollection(SECOND_COLLECTION_NAME)
public class SecondController {
    public static final String SECOND_COLLECTION_NAME = "second";

    @GetMapping("/second")
    @UsePostmanFolderPath("folder1")
    public ResponseEntity<EchoController.Response> second(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ResponseEntity.ok(new EchoController.Response(message));
    }
}
