package com.example.putrestaurant;


import com.example.putrestaurant.controller.RestaurantController;
import com.example.putrestaurant.dto.RestaurantDTO;
import com.example.putrestaurant.service.RestaurantService;
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

@WebMvcTest(controllers = RestaurantController.class)
@Import(RestaurantControllerTest.MockConfig.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public RestaurantService restaurantService() {
            return Mockito.mock(RestaurantService.class);
        }
    }

    private RestaurantDTO getSampleDTO() {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("Test Restaurant");
        dto.setCity("Rome");

        return dto;
    }

    @Test
    void testCreateRestaurant_success() throws Exception {
        RestaurantDTO dto = getSampleDTO();

        Mockito.doNothing().when(restaurantService).processRestaurant(Mockito.any(), Mockito.eq(false));

        mockMvc.perform(post("/api/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tour operator creato con successo."));
    }

    @Test
    void testUpdateRestaurant_success() throws Exception {
        RestaurantDTO dto = getSampleDTO();

        Mockito.doNothing().when(restaurantService).processRestaurant(Mockito.any(), Mockito.eq(true));

        mockMvc.perform(put("/api/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tour operator aggiornato con successo."));
    }

}
