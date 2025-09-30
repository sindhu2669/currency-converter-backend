package com.example.currency_convertor.controller;

import com.example.currency_convertor.model.dto.ConvertResponse;
import com.example.currency_convertor.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void testConvertEndpoint() throws Exception {
        // ✅ Build mock response
        ConvertResponse response = new ConvertResponse("USD", "INR", 10L, 825.0, 82.5);

        // ✅ Mock service method
        Mockito.when(currencyService.convert("USD", "INR", 10L)).thenReturn(response);

        // ✅ Perform GET request
        mockMvc.perform(get("/convert")
                        .param("from", "USD")
                        .param("to", "INR")
                        .param("amount", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("INR"))
                .andExpect(jsonPath("$.amount").value(10))
                .andExpect(jsonPath("$.convertedAmount").value(825.0));
    }
}
