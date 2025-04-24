package com.example.putAccommodation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PutAccommodationControllerTest {

        @Mock
        private PutAccommodationService putAccommodationService;

        @InjectMocks
        private PutAccommodationController putAccommodationController;

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
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
            request.setAccomodationType(AccomodationType.HOTEL);
            request.setDescription("Descrizione");
            return request;
        }

        @Test
        void testPutAccommodation_Success() {
            PutAccommodationRequest request = getMockRequest();
            doNothing().when(putAccommodationService).putAccommodation(request, true);

            ResponseEntity<String> response = putAccommodationController.putAccommodation(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Aggiornamento avvenuto con successo", response.getBody());
            verify(putAccommodationService).putAccommodation(request, true);
        }

        @Test
        void testPutAccommodation_Failure() {
            PutAccommodationRequest request = getMockRequest();
            doThrow(new RuntimeException("Internal error")).when(putAccommodationService).putAccommodation(request, true);

            ResponseEntity<String> response = putAccommodationController.putAccommodation(request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().contains("Errore interno al servizio"));
        }

        @Test
        void testPostAccommodation_Success() {
            PutAccommodationRequest request = getMockRequest();
            doNothing().when(putAccommodationService).putAccommodation(request, false);

            ResponseEntity<String> response = putAccommodationController.postAccommodation(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Inserimento avvenuto con successo", response.getBody());
            verify(putAccommodationService).putAccommodation(request, false);
        }

        @Test
        void testPostAccommodation_Failure() {
            PutAccommodationRequest request = getMockRequest();
            doThrow(new RuntimeException("Internal error")).when(putAccommodationService).putAccommodation(request, false);

            ResponseEntity<String> response = putAccommodationController.postAccommodation(request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().contains("Errore interno al servizio"));
        }

        @Test
        void testPutAccommodation_Timeout() {
            PutAccommodationRequest request = getMockRequest();
            doThrow(new ResourceAccessException("Timeout")).when(putAccommodationService).putAccommodation(request, true);

            ResponseEntity<String> response = putAccommodationController.putAccommodation(request);

            assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
            assertTrue(response.getBody().contains("Timeout nel contattare il servizio esterno"));
        }

        @Test
        void testPostAccommodation_Timeout() {
            PutAccommodationRequest request = getMockRequest();
            doThrow(new ResourceAccessException("Timeout")).when(putAccommodationService).putAccommodation(request, false);

            ResponseEntity<String> response = putAccommodationController.postAccommodation(request);

            assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
            assertTrue(response.getBody().contains("Timeout nel contattare il servizio esterno"));
        }
    }

