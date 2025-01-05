package dev.jora.postman4j.endpoints;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import dev.jora.postman4j.annotations.UsePostmanResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.jora.postman4j.endpoints.EchoController.ECHO_COLLECTION_NAME;


/**
 * @author dyadyaJora on 30.12.2024
 */
@Slf4j
@RestController
@RequestMapping(produces = "application/json")
@UsePostmanCollection(ECHO_COLLECTION_NAME)
public class EchoController {
    public static final String ECHO_COLLECTION_NAME = "echo";
    public static final String ECHO_ERROR_COLLECTION_NAME = "echo-error";

    @GetMapping("/echo")
    @UsePostmanFolderPath("folder1")
    @UsePostmanRequest("list echo")
    @UsePostmanResponse("list echo 200")
    public ResponseEntity<Response> echo(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ResponseEntity.ok(new Response(message));
    }

    @UsePostmanCollection(ECHO_ERROR_COLLECTION_NAME)
    @GetMapping("/echo-error")
    @UsePostmanRequest("list echo-error")
    public ResponseEntity<Response> echoError(
            HttpServletRequest request,
            @RequestParam(value = "message", defaultValue = "Hello") String message) throws IllegalAccessException {
        throw new IllegalAccessException("Error message");
    }

    @PostMapping("/echo")
    @UsePostmanFolderPath("folder2")
    @UsePostmanRequest("save echo")
    @UsePostmanResponse("save echo 200")
    public ResponseEntity<Response> echoPost(@RequestBody Message message) {
        return ResponseEntity.ok(new Response(message.getContent()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String content;
    }
}