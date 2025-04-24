package com.example.putAtecoNew;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

public class PutAtecoNewControllerTest {
    @Mock
    private PutAtecoNewService putAtecoNewService;

    @InjectMocks
    private PutAtecoNewController putAtecoNewController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private PutAtecoNewRequest getMockRequest() {
        PutAtecoNewRequest request = new PutAtecoNewRequest();
        request.setIdentifier("test-id");
        request.setFiscalCod("12345678901");
        request.setName("Ristorante Bella Vita");
        request.setRegion("Lazio");
        request.setCity("Roma");
        request.setPhotos(List.of("http://image1.jpg", "http://image2.jpg"));
        request.setFullAddress("Via Roma 10, Roma");
        request.setPhoneNumber("+390612345678");
        request.setDescription("Descrizione");
        return request;
    }

    @Test
    void testPutAtecoNew_Success() {
        PutAtecoNewRequest request = getMockRequest();
        doNothing().when(putAtecoNewService).putAtecoNew(request, true);

        ResponseEntity<String> response = putAtecoNewController.putAtecoNew(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Aggiornamento avvenuto con successo", response.getBody());
        verify(putAtecoNewService).putAtecoNew(request, true);
    }

    @Test
    void testPutAtecoNew_Failure() {
        PutAtecoNewRequest request = getMockRequest();
        doThrow(new RuntimeException("Internal error")).when(putAtecoNewService).putAtecoNew(request, true);

        ResponseEntity<String> response = putAtecoNewController.putAtecoNew(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Errore interno al servizio"));
    }

    @Test
    void testPostAtecoNew_Success() {
        PutAtecoNewRequest request = getMockRequest();
        doNothing().when(putAtecoNewService).putAtecoNew(request, false);

        ResponseEntity<String> response = putAtecoNewController.postAtecoNew(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inserimento avvenuto con successo", response.getBody());
        verify(putAtecoNewService).putAtecoNew(request, false);
    }

    @Test
    void testPostAtecoNew_Failure() {
        PutAtecoNewRequest request = getMockRequest();
        doThrow(new RuntimeException("Internal error")).when(putAtecoNewService).putAtecoNew(request, false);

        ResponseEntity<String> response = putAtecoNewController.postAtecoNew(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Errore interno al servizio"));
    }

    @Test
    void testPutAtecoNew_Timeout() {
        PutAtecoNewRequest request = getMockRequest();
        doThrow(new ResourceAccessException("Timeout")).when(putAtecoNewService).putAtecoNew(request, true);

        ResponseEntity<String> response = putAtecoNewController.putAtecoNew(request);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertTrue(response.getBody().contains("Timeout nel contattare il servizio esterno"));
    }

    @Test
    void testPostAtecoNew_Timeout() {
        PutAtecoNewRequest request = getMockRequest();
        doThrow(new ResourceAccessException("Timeout")).when(putAtecoNewService).putAtecoNew(request, false);

        ResponseEntity<String> response = putAtecoNewController.postAtecoNew(request);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertTrue(response.getBody().contains("Timeout nel contattare il servizio esterno"));
    }
}
