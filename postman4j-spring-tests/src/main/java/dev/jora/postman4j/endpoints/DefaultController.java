package dev.jora.postman4j.endpoints;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dyadyaJora on 05.01.2025
 */
@RestController
public class DefaultController {
    @GetMapping("/default")
    public ResponseEntity<PredefinedResponse> getPredefinedResponse(@RequestParam(value = "retry", defaultValue = "0") String retry) {
        PredefinedResponse response = new PredefinedResponse("This is a predefined message", 200);
        return ResponseEntity.ok(response);
    }

    @Data
    @AllArgsConstructor
    public static class PredefinedResponse {
        private String message;
        private int code;
    }
}
