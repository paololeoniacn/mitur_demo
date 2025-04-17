package com.example.putAccommodation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

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
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE); // setta la data e ora corrente

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
}
