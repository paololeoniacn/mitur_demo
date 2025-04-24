package com.example.demo;

import com.example.normalizename.Slugifier;
import org.springframework.stereotype.Service;

@Service
public class SlugifyService {
    public String normalize(String input){
        String normalized = Slugifier.slugify(input);

        if (normalized.matches("(^[a-zA-Z0-9]{2}$|^[a-zA-Z0-9]{2}[-_][a-zA-Z0-9]{2}$)")) {
            return "folder_" + normalized;
        } else {
            return normalized;
        }
    }
}
