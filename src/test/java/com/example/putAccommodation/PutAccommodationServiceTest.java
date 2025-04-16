package com.example.putAccommodation;

import com.example.demo.SlugifyService;
import com.example.uploadImage.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PutAccommodationServiceTest {
    private S3Service s3Service;
    private ObjectMapper objectMapper;
    private SlugifyService slugifyService;
    private PutAccommodationService putAccommodationService;

    @BeforeEach
    void setUp() {
        s3Service = mock(S3Service.class);
        objectMapper = mock(ObjectMapper.class);
        slugifyService = mock(SlugifyService.class);
        putAccommodationService = new PutAccommodationService(s3Service, objectMapper, slugifyService);
    }

    @Test
    void testPutAccommodation_AllFieldsCorrectlySet() throws IOException {
        PutAccommodationRequest request = getMockRequest();
        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(s3Service.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("s3/path/image.jpg");

        putAccommodationService.putAccommodation(request);

        verify(s3Service, times(request.getPhotos().size()))
                .uploadImageFromUrl(anyString(), eq("bucketName"), eq("s3/path/image.jpg"));
    }

    @Test
    void testPostAccommodation_AllFieldsCorrectlySet() throws IOException {
        PutAccommodationRequest request = getMockRequest();
        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(s3Service.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("s3/path/image.jpg");

        putAccommodationService.postAccommodation(request);

        verify(s3Service, times(request.getPhotos().size()))
                .uploadImageFromUrl(anyString(), eq("bucketName"), eq("s3/path/image.jpg"));
    }

    @Test
    void testRenderJson_MappingCorrectly() {
        PutAccommodationRequest request = getMockRequest();
        List<String> images = List.of("img1.jpg", "img2.jpg");

        RenderAccommodationAEM json = putAccommodationService.renderJson(request, images);

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
        PutAccommodationRequest request = new PutAccommodationRequest(); // Empty request likely to fail
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
