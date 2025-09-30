package com.example.currency_convertor.controller;

import com.example.currency_convertor.model.ConversionRate;
import com.example.currency_convertor.model.dto.ConvertRequest;
import com.example.currency_convertor.model.dto.ConvertResponse;
import com.example.currency_convertor.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CurrencyController {

    private static final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convert(@RequestBody ConvertRequest request) {
        try {
            validateRequest(request);
            ConvertResponse response = service.convert(
                    request.getFromCurrency(),
                    request.getToCurrency(),
                    request.getAmount()
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error in /convert: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Conversion failed", "message", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConversionRate>> getHistory() {
        try {
            return ResponseEntity.ok(service.getConversionHistory());
        } catch (Exception e) {
            log.error("Error fetching history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getCurrencies() {
        try {
            return ResponseEntity.ok(service.getAllCurrencyCodes());
        } catch (Exception e) {
            log.error("Error fetching currencies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/currencies/full")
    public ResponseEntity<List<String>> getCurrenciesWithNames() {
        try {
            return ResponseEntity.ok(service.getAllCurrencies());
        } catch (Exception e) {
            log.error("Error fetching currencies with names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Currency Converter API"
        ));
    }

    private void validateRequest(ConvertRequest request) {
        if (request.getFromCurrency() == null || request.getFromCurrency().isEmpty()) {
            throw new IllegalArgumentException("From currency is required");
        }
        if (request.getToCurrency() == null || request.getToCurrency().isEmpty()) {
            throw new IllegalArgumentException("To currency is required");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }
}
