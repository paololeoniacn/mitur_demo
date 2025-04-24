package com.example.puttouroperator;


import com.example.puttouroperator.controller.TourOperatorController;
import com.example.puttouroperator.dto.TourOperatorDTO;
import com.example.puttouroperator.service.TourOperatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TourOperatorController.class)
@Import(TourOperatorControllerTest.MockConfig.class)
class TourOperatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TourOperatorService tourOperatorService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TourOperatorService tourOperatorService() {
            return Mockito.mock(TourOperatorService.class);
        }
    }

    private TourOperatorDTO getSampleDTO() {
        TourOperatorDTO dto = new TourOperatorDTO();
        dto.setName("Test Tour");
        dto.setCity("Roma");
        dto.setRegion("Lazio");
        return dto;
    }

    @Test
    void testCreateTourOperator_success() throws Exception {
        TourOperatorDTO dto = getSampleDTO();

        Mockito.doNothing().when(tourOperatorService).processTourOperator(Mockito.any(), Mockito.eq(false));

        mockMvc.perform(post("/api/tour-operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tour operator creato con successo."));
    }

    @Test
    void testUpdateTourOperator_success() throws Exception {
        TourOperatorDTO dto = getSampleDTO();

        Mockito.doNothing().when(tourOperatorService).processTourOperator(Mockito.any(), Mockito.eq(true));

        mockMvc.perform(put("/api/tour-operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tour operator aggiornato con successo."));
    }

}
