package com.example.currency_convertor.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyBeaconClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public CurrencyBeaconClient(RestTemplate restTemplate,
                                @Value("${currency.api.base-url}") String baseUrl,
                                @Value("${currency.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Fetch a specific conversion rate from Currency Beacon API.
     * If API fails → fallback mock rate (1.0).
     */
    public Double getConversionRate(String from, String to) {
        String url = baseUrl + "/convert?api_key=" + apiKey + "&from=" + from + "&to=" + to + "&amount=1";

        try {
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("response")) {
                Map<String, Object> res = (Map<String, Object>) response.get("response");
                if (res.containsKey("value")) {
                    return Double.valueOf(res.get("value").toString());
                }
            }
            throw new RuntimeException("No value in API response");
        } catch (Exception e) {
            System.err.println("⚠️ API failed, using fallback rate. Error: " + e.getMessage());
            // Fallback mock rate for demo purpose
            return 1.0;
        }
    }

    /**
     * Fetch all supported currencies (code -> name) from Currency Beacon API.
     * If API fails → return fallback major currencies.
     */
    public Map<String, String> getAllCurrencies() {
        String url = baseUrl + "/currencies?api_key=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("response")) {
                Object responseData = response.get("response");
                if (responseData instanceof Map) {
                    return (Map<String, String>) responseData;
                }
            }
            throw new RuntimeException("No currencies in API response");
        } catch (Exception e) {
            System.err.println("⚠️ Error fetching currencies, using fallback list. Error: " + e.getMessage());
            // Return a default set of major currencies as fallback
            Map<String, String> fallback = new HashMap<>();
            fallback.put("USD", "US Dollar");
            fallback.put("EUR", "Euro");
            fallback.put("GBP", "British Pound");
            fallback.put("INR", "Indian Rupee");
            fallback.put("JPY", "Japanese Yen");
            fallback.put("AUD", "Australian Dollar");
            fallback.put("CAD", "Canadian Dollar");
            fallback.put("CHF", "Swiss Franc");
            fallback.put("CNY", "Chinese Yuan");
            return fallback;
        }
    }
}
