package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SlugifyController.class)
class SlugifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testNormalizeEndpoint() throws Exception {
        mockMvc.perform(get("/api/normalize")
                        .param("input", "Esempio di Input"))
                .andExpect(status().isOk())
                .andExpect(content().string("esempio-di-input"));
    }
}
