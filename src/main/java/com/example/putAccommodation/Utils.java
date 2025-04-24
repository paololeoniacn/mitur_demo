package com.example.putAccommodation;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class Utils {

    private static String privateKey;

    @Value("${private.key}")
    private String injectedApiKey;

    @PostConstruct
    public void init() {
        privateKey = injectedApiKey;
    }

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
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        URL url = new URL(imageUrl); // Crea l'URL da dove scaricare l'immagine
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Apre la connessione HTTP
        connection.setRequestMethod("GET");
        String apiKey = encryptWithPrivateKey("ext_mitur|"+currentDate, privateKey); // crea apiKey
        connection.setRequestProperty("X-API-Key", apiKey); // Aggiunge ApiKey all'intestazione della richiesta
        //System.out.println("API Key: " + apiKey);
        connection.setRequestProperty("X-Product", "ext_mitur");
        int status = connection.getResponseCode();
        System.out.println("Status Code: " + status);

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

    public static String encryptWithPrivateKey(String plaintext, String privateKeyBase64) {
        try {
            // Decodifica la chiave privata dalla stringa in base64
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // Crea il cifrario RSA con il padding PKCS1
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            // Cifra il testo in chiaro
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));

            // Converte i byte cifrati in una stringa base64
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
