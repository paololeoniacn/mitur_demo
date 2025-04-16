package com.example.persistedquery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersistedQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersistedQueryController controller;

    private MockRestServiceServer mockServer;

    @Value("${external.api.url}")
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        // Usa reflection per accedere al campo privato restTemplate
        Field field = PersistedQueryController.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) field.get(controller);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testForwardRequest_withMatrixParams() throws Exception {
        String param = "myQuery";
        String matrix = ";foo=bar;baz=123";
        String expectedUrl = baseUrl + "/" + param + matrix + ";";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("response from external", MediaType.TEXT_PLAIN));

        mockMvc.perform(get("/persistedQuery/myQuery;foo=bar;baz=123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("response from external")));

        mockServer.verify();
    }
}
