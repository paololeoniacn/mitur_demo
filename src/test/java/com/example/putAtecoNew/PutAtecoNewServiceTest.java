package com.example.putAtecoNew;

import com.example.demo.SlugifyService;
import com.example.putAccommodation.Utils;
import com.example.uploads3aem.S3Request;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PutAtecoNewServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private SlugifyService slugifyService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PutAtecoNewService putAtecoNewService;

    private PutAtecoNewRequest testRequest;

    @BeforeEach
    void setup() {
        testRequest = new PutAtecoNewRequest();
        testRequest.setName("Test Hotel");
        testRequest.setRegion("Lombardy");
        testRequest.setCity("Milan");
        testRequest.setCountry("Italy");
        testRequest.setPhotos(Arrays.asList("http://example.com/photo1.jpg", "http://example.com/photo2.jpg"));
        testRequest.setIdentifier("test-id-123");
        testRequest.setFiscalCod("FISCAL123");
        testRequest.setFullAddress("Test Address");
        testRequest.setPhoneNumber("+123456789");
        testRequest.setDescription("Test description");
        testRequest.setPrimaryAtecoCod("82.40");
    }

    @Test
    void testPutAtecoNew_shouldProcessRequestSuccessfully() throws Exception {

        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(objectMapper.writeValueAsString(any())).thenReturn("jsonString");

        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            utilsMock.when(() -> Utils.downloadImage(anyString()))
                    .thenReturn(new byte[]{1, 2, 3});
            utilsMock.when(() -> Utils.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("/mock/path/to/image.jpg");

            putAtecoNewService.putAtecoNew(testRequest, true);

            verify(s3Service, times(2)).uploadImage(any(), anyString());
            verify(s3Service).process(any(S3Request.class));
        }
    }

    @Test
    void postAccommodation_shouldProcessRequestSuccessfully() throws Exception {
        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(objectMapper.writeValueAsString(any())).thenReturn("jsonString");

        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            utilsMock.when(() -> Utils.downloadImage(anyString()))
                    .thenReturn(new byte[]{1, 2, 3});
            utilsMock.when(() -> Utils.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("/mock/path/to/image.jpg");

            putAtecoNewService.putAtecoNew(testRequest, false);

            verify(s3Service, times(2)).uploadImage(any(), anyString());
            verify(s3Service).process(any(S3Request.class));
        }
    }

    @Test
    void renderAccommodation_shouldMapRequestToAEMCorrectly() {
        List<String> imagePaths = Arrays.asList("/path/to/image1.jpg", "/path/to/image2.jpg");

        RenderAtecoNewAEM result = putAtecoNewService.renderAtecoNew(testRequest, imagePaths);

        assertNotNull(result);
        assertEquals(testRequest.getName(), result.getInsegna());
        assertEquals(testRequest.getName(), result.getOfficialName());
        assertEquals(testRequest.getCity(), result.getCity());
        assertEquals(testRequest.getRegion(), result.getRegion());
        assertEquals(testRequest.getCountry(), result.getCountry());
        assertEquals(imagePaths, result.getImages());
        assertEquals(testRequest.getPrimaryAtecoCod(), result.getPrimaryAtecoCode());
    }

    @Test
    void renderJsonToString_shouldReturnJsonString() throws JsonProcessingException {
        RenderAtecoNewAEM aem = new RenderAtecoNewAEM();
        String expectedJson = "{}";
        when(objectMapper.writeValueAsString(aem)).thenReturn(expectedJson);
        String result = putAtecoNewService.renderJsonToString(aem);
        assertEquals(expectedJson, result);
    }

    @Test
    void renderJsonToString_shouldThrowRuntimeExceptionOnError() throws JsonProcessingException {
        RenderAtecoNewAEM aem = new RenderAtecoNewAEM();
        when(objectMapper.writeValueAsString(aem)).thenThrow(new JsonProcessingException("Error") {});
        assertThrows(RuntimeException.class, () -> putAtecoNewService.renderJsonToString(aem));
    }

    @Test
    void pathBuilderJson_shouldBuildCorrectPathForPut() {
        String result = putAtecoNewService.pathBuilderJson(false, "hotel", "milan", "lombardy", "/base/path/");
        assertTrue(result.contains("destination_put"));
    }

    @Test
    void pathBuilderJson_shouldBuildCorrectPathForUpdate() {
        String result = putAtecoNewService.pathBuilderJson(true, "hotel", "milan", "lombardy", "/base/path/");
        assertTrue(result.contains("destination_update_"));
    }

    @Test
    void pathBuilderJson_shouldTruncateLongNames() {
        String longName = "This is a very long hotel name that exceeds eighty characters and should be truncated for the path";
        String result = putAtecoNewService.pathBuilderJson(false, longName, "Milan", "Lombardy", "/base/path/");
        assertTrue(result.contains("this is a very long hotel name that exceeds eighty characters and should be trunca"));
    }
}
