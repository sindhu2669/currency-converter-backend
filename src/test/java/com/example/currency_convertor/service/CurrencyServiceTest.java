package com.example.currency_convertor.service;

import com.example.currency_convertor.model.dto.ConvertResponse;
import com.example.currency_convertor.repository.ConversionRateRepository;
import com.example.currency_convertor.service.external.CurrencyBeaconClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {

    @Mock
    private ConversionRateRepository rateRepository;

    @Mock
    private CurrencyBeaconClient beaconClient;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvert() {
        // ✅ Mock API response
        when(beaconClient.getConversionRate("USD", "INR")).thenReturn(82.5);

        // ✅ Call service
        ConvertResponse response = currencyService.convert("USD", "INR", 10L);

        // ✅ Verify
        assertEquals("USD", response.getFrom());
        assertEquals("INR", response.getTo());
        assertEquals(10L, response.getAmount());
        assertEquals(825.0, response.getConvertedAmount());
    }
}
