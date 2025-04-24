package com.example.normalizename;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SlugifyController {

    private static final Logger logger = LogManager.getLogger(SlugifyController.class);

    @GetMapping("/normalize")
    public ResponseEntity<String> normalize(@RequestParam String input) {
        logger.info("Received input: {}", input);

        try {
            String normalized = Slugifier.slugify(input);

            if (normalized.matches("(^[a-zA-Z0-9]{2}$|^[a-zA-Z0-9]{2}[-_][a-zA-Z0-9]{2}$)")) {
                String folderName = "folder_" + normalized;
                logger.info("Returning normalized value concat to folder_: {}", folderName);
                return ResponseEntity.ok(folderName);
            } else {
                logger.info("Returning normalized value: '{}'", normalized);
                return ResponseEntity.ok(normalized);
            }

        } catch (Exception e) {
            logger.error("Exception occurred during normalization", e);
            return ResponseEntity
                    .internalServerError()
                    .body("An error occurred while processing your request.");
        }
    }
}
