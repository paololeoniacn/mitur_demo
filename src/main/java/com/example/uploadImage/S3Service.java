package com.example.uploadImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class S3Service {

    private final S3Client s3Client;

    private static final Logger logger = LogManager.getLogger(S3Service.class);

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // caricamento immagine su S3 -> TODO: aggiungere apiKey
    public void uploadImageFromUrl(String imageUrl, String bucketName, String finalPath) throws IOException {
        try (InputStream inputStream = new URL(imageUrl).openStream()) { // scarica immagine da URL remoto
            // Legge tutto il contenuto dell'immagine come byte[]
            byte[] imageBytes = inputStream.readAllBytes();

            // Crea richiesta di upload
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(finalPath)
                    .contentType("image/jpeg") // o image/png se serve
                    .build();

            // Carica su S3 direttamente da byte[]
            s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes));
            logger.info("Immagine correttamente caricata su S3");
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il download o l'upload dell'immagine", e);
        }
    }

    public String assignIdentifierPhoto(String imageUrl){
        int downloadIndex = imageUrl.indexOf("/download");
        if(downloadIndex == -1){
            return imageUrl;
        }
        String beforeDownload = imageUrl.substring(0, downloadIndex); // Prende tutto prima di "/download"
        int lastSlashIndex = beforeDownload.lastIndexOf('/'); // Trova l'ultimo slash nella parte "beforeDownload"
        if (lastSlashIndex == -1) {
            return beforeDownload;
        }
        return beforeDownload.substring(lastSlashIndex + 1);  // Restituisce l'ultima parte dopo l'ultimo slash
    }

    public String pathBuilder(String imageUrl, String name, String region, String city, String initialPath){
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE); // setta la data e ora corrente

        String identifier = assignIdentifierPhoto(imageUrl);

        String basePath = initialPath.endsWith("/") ? initialPath : initialPath + "/";
        String finalPath = basePath +
                region.toLowerCase() + "/" +
                city.toLowerCase() + "/" +
                name.toLowerCase() + "/media/" +
                currentDate + "_" +
                name.toLowerCase() + "_" +
                //indexPhoto + "_" +
                identifier + ".jpg";
        return finalPath;
    }

    public String pathBuilderJson(Boolean isUpdate, String name, String city, String region, String initialPathAccommodation){
        String finalName = name.length() > 80 ? name.substring(0, 80) : name;
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        if(isUpdate){
            String finalPath = initialPathAccommodation
                    + region.toLowerCase() + "/"
                    + city.toLowerCase() + "/"
                    + name.toLowerCase() + "/"
                    + "destination_update_" + finalName
                    + "_accommodation_"
                    + timeStamp;
            return finalPath;
        } else {
            String finalPath = initialPathAccommodation
                    + region.toLowerCase() + "/"
                    + city.toLowerCase() + "/"
                    + name.toLowerCase() + "/"
                    + "destination_put_" + finalName
                    + "_accommodation_"
                    + timeStamp;
            return finalPath;
        }
    }
}

