package com.example.putrestaurant.service;

import com.example.putrestaurant.client.NormalizeNameClient;
import com.example.putrestaurant.dto.RestaurantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    private final NormalizeNameClient normalizeNameClient;
    private final ImageUploaderService imageUploader;
    private final S3AemUploaderService s3AemUploader;
    private final CrmSenderService crmSender;

    @Value("${s3.path.initial.restaurant}")
    private String initialPathRestaurant;

    public RestaurantService(NormalizeNameClient normalizeNameClient, ImageUploaderService imageUploader,
                             S3AemUploaderService s3AemUploader,
                             CrmSenderService crmSender) {
        this.normalizeNameClient = normalizeNameClient;
        this.imageUploader = imageUploader;
        this.s3AemUploader = s3AemUploader;
        this.crmSender = crmSender;
    }

    public void processRestaurant(RestaurantDTO dto, boolean isUpdate) {
        logger.info("Starting process for TourOperator: {}, isUpdate: {}", dto.getName(), isUpdate);

        imageUploader.upload(dto);
        logger.info("Image uploaded for TourOperator: {}", dto.getName());

        String path = buildPath(dto, isUpdate);
        logger.info("Generated path: {}", path);

        s3AemUploader.upload(dto, path);
        logger.info("Uploaded to S3/AEM path: {}", path);

        crmSender.send(dto);
        logger.info("CRM data sent for TourOperator: {}", dto.getName());
    }

    public String buildPath(RestaurantDTO dto, boolean isUpdate) {
        String region = normalizeNameClient.normalize(dto.getRegion());
        String city = normalizeNameClient.normalize(dto.getCity());
        String name = normalizeNameClient.normalize(dto.getName());

        String normalizedName = name.length() > 80 ? name.substring(0, 80) : name;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String actionPrefix = isUpdate ? "destination_update_" : "destination_put_";

        String cleanedBasePath = initialPathRestaurant.endsWith("/") ? initialPathRestaurant.substring(0, initialPathRestaurant.length() - 1) : initialPathRestaurant;

        return String.join("/",
                cleanedBasePath,
                region,
                city,
                name,
                actionPrefix + normalizedName + "_restaurant_" + timestamp
        );
    }

}
