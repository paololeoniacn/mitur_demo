package com.example.deleteaccommodation;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
class AccommodationService {

    private static final String BASE_PATH_RESTAURANT = "/tmp/s3/restaurants/";
    private static final String BASE_PATH_ACCOMMODATION = "/tmp/s3/accommodations/";
    private static final String BASE_PATH_TOUR_GUIDES = "/tmp/s3/tourist-guides/";
    private static final String BASE_PATH_TOUR_OPERATORS = "/tmp/s3/tour-operators/";

    public void deleteAccommodation(String identifier, String type, String primaryAtecoCod, String startDateAtecoNew) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fullPath;

        if ("restaurants".equals(type)) {
            fullPath = BASE_PATH_RESTAURANT + "deleted/destination_delete_" + identifier + "_restaurant_" + timestamp;
        } else if ("accommodation".equals(type)) {
            fullPath = BASE_PATH_ACCOMMODATION + "deleted/destination_delete_" + identifier + "_accommodation_" + timestamp;
        } else if ("tourOperators".equals(type) && isTouristGuide(primaryAtecoCod, startDateAtecoNew)) {
            fullPath = BASE_PATH_TOUR_GUIDES + "deleted/destination_delete_" + identifier + "_tourist-guide_" + timestamp;
        } else {
            fullPath = BASE_PATH_TOUR_OPERATORS + "deleted/destination_delete_" + identifier + "_tour-operator_" + timestamp;
        }

        Path path = Paths.get(fullPath);
        Files.createDirectories(path);
        System.out.println("Uploading to S3 from path: " + fullPath);
        System.out.println("Deleting accommodation with ID: " + identifier);
    }

    private boolean isTouristGuide(String atecoCod, String startDateStr) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(startDate)) {
                return "79.90.2".equals(atecoCod) || "79.90.20".equals(atecoCod) || "93.19.92".equals(atecoCod);
            } else {
                return atecoCod != null && (
                        atecoCod.equals("79.90.0") ||
                                atecoCod.equals("79.90.01") ||
                                atecoCod.equals("79.90.02") ||
                                atecoCod.equals("79.90.03") ||
                                atecoCod.equals("93.19.92")
                );
            }
        } catch (Exception e) {
            return false;
        }
    }
}