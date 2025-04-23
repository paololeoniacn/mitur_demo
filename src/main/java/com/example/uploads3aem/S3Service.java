package com.example.uploads3aem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void process(S3Request request) {
        String basePath = request.getGetPathS3();
        String jsonPath = basePath + ".json";
        String trgPath = basePath + ".trg";

        String backupPath = String.format("backup/%s/%s.json",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                basePath);

        putObject(jsonPath, request.getJsonString());
        putObject(backupPath, request.getJsonString());
        putObject(trgPath, "");
    }

    private void putObject(String key, String content) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(putRequest, RequestBody.fromString(content));
    }

    public void uploadImage(byte[] imageBytes, String finalPath) throws IOException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(finalPath)
                .build();
        s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes));
    }
}
