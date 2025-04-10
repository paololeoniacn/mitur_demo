package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SlugifyController {

    @GetMapping("/normalize")
    public String normalize(@RequestParam String input) {
        String normalized = Slugifier.slugify(input);

        if (!normalized.isEmpty()) {
            return "folder_" + normalized;
        } else {
            return "";
        }
    }
}
