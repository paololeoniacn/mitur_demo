package com.example.putAccommodation;

import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    @Value("${api.key}")
    private static String apiKey;

    public static String assignIdentifierPhoto(String imageUrl){
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

    public static String pathBuilder(String imageUrl, String name, String region, String city, String initialPath, String indexPhoto){
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")); // setta la data e ora corrente

        String identifier = assignIdentifierPhoto(imageUrl);

        String basePath = initialPath.endsWith("/") ? initialPath : initialPath + "/";
        String finalPath = basePath +
                region.toLowerCase() + "/" +
                city.toLowerCase() + "/" +
                name.toLowerCase() + "/media/" +
                currentDate + "_" +
                name.toLowerCase() + "_" +
                indexPhoto + "_" +
                identifier + ".jpg";
        return finalPath;
    }

    public static byte[] downloadImage(String imageUrl) throws Exception{
        URL url = new URL(imageUrl); // Crea l'URL da dove scaricare l'immagine
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Apre la connessione HTTP
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-API-Key", apiKey); // Aggiunge ApiKey all'intestazione della richiesta
        // System.out.println("API Key: " + apiKey);
        int status = connection.getResponseCode();
        // System.out.println("Status Code: " + status);

        if (status != 200) {
            throw new RuntimeException("Errore HTTP: " + status);
        }

        try (InputStream inputStream = connection.getInputStream(); // Ottiene l'input stream dalla connessione
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) { // Legge i dati in byte
            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                outputStream.write(byteRead);
            }
            return outputStream.toByteArray();  // Restituisce i byte dell'immagine
        }
    }

}
