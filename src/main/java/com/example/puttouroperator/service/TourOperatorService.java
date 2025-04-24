package com.example.puttouroperator.service;


import com.example.puttouroperator.client.NormalizeNameClient;
import com.example.puttouroperator.dto.TourOperatorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TourOperatorService {

    private static final Logger logger = LoggerFactory.getLogger(TourOperatorService.class);

    private final NormalizeNameClient normalizeNameClient;
    private final ImageUploaderService imageUploader;
    private final S3AemUploaderService s3AemUploader;
    private final CrmSenderService crmSender;

    @Value("${s3.path.initial.tourOperator}")
    private String initialPathTourOperator;

    @Value("${start.date.ateco.new}")
    private String startDateRaw;

    @Value("${initial.path.tourist.guides}")
    private String initialPathTouristGuides;

    public TourOperatorService(NormalizeNameClient normalizeNameClient, ImageUploaderService imageUploader,
                               S3AemUploaderService s3AemUploader,
                               CrmSenderService crmSender) {
        this.normalizeNameClient = normalizeNameClient;
        this.imageUploader = imageUploader;
        this.s3AemUploader = s3AemUploader;
        this.crmSender = crmSender;
    }

    public void processTourOperator(TourOperatorDTO dto, boolean isUpdate) {
        logger.info("Starting process for TourOperator: {}, isUpdate: {}", dto.getName(), isUpdate);

        imageUploader.upload(dto);
        logger.info("Image uploaded for TourOperator: {}", dto.getName());

        String path = checkAtecoAndGeneratePath(dto, isUpdate);
        logger.info("Generated path: {}", path);

        s3AemUploader.upload(dto, path);
        logger.info("Uploaded to S3/AEM path: {}", path);

        crmSender.send(dto);
        logger.info("CRM data sent for TourOperator: {}", dto.getName());
    }

    public String checkAtecoAndGeneratePath(TourOperatorDTO dto, boolean isUpdate) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDate = OffsetDateTime.parse(startDateRaw);

        String ateco = dto.getPrimaryAtecoCod();
        logger.debug("Checking ATECO code: {} for TourOperator: {}", ateco, dto.getName());

        boolean shouldBuildTouristGuidesPath;

        if (now.isBefore(startDate)) {
            shouldBuildTouristGuidesPath = ateco.equals("79.90.2") || ateco.equals("79.90.20") || ateco.equals("93.19.92");
        } else {
            List<String> validCodes = List.of("79.90.0", "79.90.01", "79.90.02", "79.90.03", "93.19.92");
            shouldBuildTouristGuidesPath = validCodes.contains(ateco);
        }

        if (shouldBuildTouristGuidesPath) {
            logger.info("ATECO code matches tourist guide path logic");
            return generatePathTouristGuides(dto, isUpdate);
        } else {
            logger.info("ATECO code matches tour operator path logic");
            return generatePathTourOperator(dto, isUpdate);
        }
    }

    private String generatePathTouristGuides(TourOperatorDTO dto, boolean isUpdate) {
        return generatePath(dto, isUpdate, initialPathTouristGuides, "tourist-guides");
    }

    private String generatePathTourOperator(TourOperatorDTO dto, boolean isUpdate) {
        return generatePath(dto, isUpdate, initialPathTourOperator, "tour-operators");
    }

    private String generatePath(
            TourOperatorDTO dto,
            boolean isUpdate,
            String basePath,
            String suffixType) {

        String region = normalizeNameClient.normalize(dto.getRegion());
        String city = normalizeNameClient.normalize(dto.getCity());
        String name = normalizeNameClient.normalize(dto.getName());

        logger.debug("Normalized region: {}, city: {}, name: {}", region, city, name);

        String normalizedName = name.length() > 80 ? name.substring(0, 80) : name;

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String action = isUpdate ? "destination_update_" : "destination_put_";
        String fileName = action + normalizedName + "_" + suffixType + "_" + timestamp;

        // Assicuriamoci che basePath non finisca con slash per evitare doppio slash
        String cleanedBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;

        // Composizione dinamica, senza delimitatori hardcoded
        return String.join("/", cleanedBasePath, region, city, name, fileName);

    }
}
