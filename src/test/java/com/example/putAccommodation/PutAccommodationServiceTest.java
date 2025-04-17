package com.example.putAccommodation;

import com.example.demo.SlugifyService;
import com.example.uploads3aem.S3Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.uploads3aem.S3Service;
import jakarta.validation.ValidationException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PutAccommodationServiceTest {

    @Mock
    private SlugifyService slugifyService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private PutAccommodationService putAccommodationService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testPutAccommodation_AllImagesUploadedSuccessfully() throws IOException {
        // Arrange
        PutAccommodationRequest request = new PutAccommodationRequest();
        request.setName("Test Name");
        request.setRegion("Test Region");
        request.setCity("Test City");
        List<String> photos = List.of("img1.jpg", "img2.jpg", "img3.jpg");
        request.setPhotos(photos);

        when(slugifyService.normalize(anyString())).thenReturn("normalized");

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.pathBuilder(
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
            )).thenAnswer(invocation -> {
                String filename = invocation.getArgument(0);
                return "s3/path/" + filename;
            });

            putAccommodationService.putAccommodation(request);

            for (String photo : photos) {
                verify(s3Service).uploadImageFromUrl(eq(photo), eq("s3/path/" + photo));
            }

            verify(s3Service, times(photos.size())).uploadImageFromUrl(anyString(), anyString());
        }
    }

    @Test
    void testPutAccommodation_UploadImages_OneFails_OthersSucceed() throws IOException {
        PutAccommodationRequest request = new PutAccommodationRequest();
        request.setName("Test Name");
        request.setRegion("Test Region");
        request.setCity("Test City");
        List<String> photos = List.of("img1.jpg", "img2.jpg", "img3.jpg");
        request.setPhotos(photos);

        when(slugifyService.normalize(anyString())).thenReturn("normalized");

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {

            utilsMockedStatic.when(() -> Utils.pathBuilder(
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
            )).thenAnswer(invocation -> {
                String fileName = invocation.getArgument(0);
                return "s3/path/" + fileName;
            });

            // Simula: img2 fallisce
            doNothing().when(s3Service).uploadImageFromUrl("img1.jpg", "s3/path/img1.jpg");
            doThrow(new IOException("Simulated failure")).when(s3Service).uploadImageFromUrl("img2.jpg", "s3/path/img2.jpg");
            doNothing().when(s3Service).uploadImageFromUrl("img3.jpg", "s3/path/img3.jpg");

            // Act
            putAccommodationService.putAccommodation(request);

            // Assert: tutte le immagini tentate
            verify(s3Service).uploadImageFromUrl("img1.jpg", "s3/path/img1.jpg");
            verify(s3Service).uploadImageFromUrl("img2.jpg", "s3/path/img2.jpg");
            verify(s3Service).uploadImageFromUrl("img3.jpg", "s3/path/img3.jpg");
            verify(s3Service, times(3)).uploadImageFromUrl(anyString(), anyString());
        }
    }

    @Test
    void testRenderJson_MappingCorrectly() {
        PutAccommodationRequest request = getMockRequest();
        List<String> images = List.of("img1.jpg", "img2.jpg");

        RenderAccommodationAEM json = putAccommodationService.renderAccommodation(request, images);

        assertEquals("test-id", json.getIdentifier());
        assertEquals("img1.jpg", json.getImages().get(0));
        assertEquals("Ristorante Bella Vita", json.getInsegna());
        assertEquals("12345678901", json.getFiscalCod());
    }

    @Test
    void testPathBuilderJson_UpdateTrue() {
        String result = putAccommodationService.pathBuilderJson(true, "hotel", "roma", "lazio", "/content/");
        assertTrue(result.contains("destination_update_"));
    }

    @Test
    void testPathBuilderJson_UpdateFalse() {
        String result = putAccommodationService.pathBuilderJson(false, "hotel", "roma", "lazio", "/content/");
        assertTrue(result.contains("destination_put_"));
    }

    @Test
    void testValidateWithXsd_InvalidRequest_ThrowsException() {
        PutAccommodationRequest request = new PutAccommodationRequest();
        assertThrows(ValidationException.class, () -> putAccommodationService.validateWithXsd(request));
    }

    @Test
    void testValidateWithXsd_Success() {
        PutAccommodationRequest request = getValidPutAccommodationRequest();
        assertDoesNotThrow(() -> putAccommodationService.validateWithXsd(request));
    }

    private PutAccommodationRequest getMockRequest() {
        PutAccommodationRequest request = new PutAccommodationRequest();
        request.setIdentifier("test-id");
        request.setFiscalCod("12345678901");
        request.setName("Ristorante Bella Vita");
        request.setRegion("Lazio");
        request.setCity("Roma");
        request.setPhotos(List.of("http://image1.jpg", "http://image2.jpg"));
        request.setFullAddress("Via Roma 10, Roma");
        request.setPhoneNumber("+390612345678");
        request.setAccomodationType("Hotel");
        request.setDescription("Descrizione");
        return request;
    }

    private PutAccommodationRequest getValidPutAccommodationRequest() {
        PutAccommodationRequest request = new PutAccommodationRequest();
        request.setIdentifier("ABC123");
        request.setFiscalCod("12345678901");
        request.setVatCod("IT98765432109");
        request.setName("Hotel Paradiso");
        request.setRegion("Toscana");
        request.setCity("Firenze");
        request.setFullAddress("Via dei Test 12, Firenze");
        request.setStreetName("Via dei Test");
        request.setStreetNumber("12");
        request.setPostalCode("50100");
        request.setCountry("Italia");
        request.setProvince("FI");
        request.setPhoneNumber("+3905567890");
        request.setPhotos(List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"));
        request.setWebSite("http://hotelparadiso.it");
        request.setMailAddress("info@hotelparadiso.it");
        request.setGoogleWebAddress("https://goo.gl/maps/xyz123");
        request.setDescription("Hotel di prova con tutti i campi validi");
        request.setIsoCertification("ISO9001");
        request.setAccomodationType("Hotel");
        request.setRating("5 stelle");
        request.setListOfService(List.of("Connessione WiFi gratuita", "Navetta aeroportuale"));
        request.setRoomService(List.of("Bagno privato", "Aria condizionata"));
        request.setPaymentMethods(List.of("Bonifico", "Carta di credito"));
        request.setCheckIn("14:00");
        request.setCheckOut("11:00");
        request.setHotelChain("LuxuryChain");
        request.setCin(List.of("CIN987654", "CINu2y3t3u2"));

        return request;
    }

}
