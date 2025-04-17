package com.example.putAccommodation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
public class UtilsTest {

    @Test
    void assignIdentifierPhoto_shouldReturnFilenameAfterLastSlashBeforeDownload() {
        String url = "https://example.com/images/gallery/12345/download/abc.jpg";
        String expected = "12345";
        String result = Utils.assignIdentifierPhoto(url);
        assertEquals(expected, result);
    }

    @Test
    void assignIdentifierPhoto_shouldReturnInputIfNoDownloadInUrl() {
        String url = "https://example.com/images/gallery/12345/view.jpg";
        String result = Utils.assignIdentifierPhoto(url);
        assertEquals(url, result);
    }

    @Test
    void assignIdentifierPhoto_shouldReturnInputIfNoSlashBeforeDownload() {
        String url = "download/image.jpg";
        String result = Utils.assignIdentifierPhoto(url);
        assertEquals(url, result); // "download" is before "/download"
    }

    @Test
    void pathBuilder_shouldBuildExpectedPath() {
        String imageUrl = "https://example.com/images/6789/download/photo.jpg";
        String name = "MyHotel";
        String region = "Toscana";
        String city = "Firenze";
        String initialPath = "/content/dam/tdh-infocamere/it/accommodations";
        String indexPhoto = "main";

        // Usa la stessa data generata dal metodo per non avere flakiness
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        String expected = "/content/dam/tdh-infocamere/it/accommodations/toscana/firenze/myhotel/media/" +
                currentDate + "_myhotel_main_6789.jpg";

        String result = Utils.pathBuilder(imageUrl, name, region, city, initialPath, indexPhoto);

        assertEquals(expected, result);
    }

    @Test
    void pathBuilder_shouldAddTrailingSlashIfMissing() {
        String imageUrl = "https://example.com/images/111/download/photo.jpg";
        String initialPath = "/base/path"; // no slash
        String result = Utils.pathBuilder(imageUrl, "Name", "Region", "City", initialPath, "photo");
        assertTrue(result.startsWith("/base/path/"), "Expected path to start with trailing slash");
    }
}
