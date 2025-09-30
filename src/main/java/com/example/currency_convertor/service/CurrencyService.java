package com.example.currency_convertor.service;

import com.example.currency_convertor.model.ConversionRate;
import com.example.currency_convertor.model.dto.ConvertResponse;
import com.example.currency_convertor.repository.ConversionRateRepository;
import com.example.currency_convertor.service.external.CurrencyBeaconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final ConversionRateRepository rateRepository;
    private final CurrencyBeaconClient beaconClient;

    // ✅ Fallback static list of ISO currencies (from IBAN list)
    private static final List<String> FALLBACK_CURRENCIES = List.of(
            "USD", "EUR", "INR", "GBP", "JPY", "AUD",
            "CAD", "CHF", "CNY", "NZD", "SGD", "ZAR", "BRL"
    );

    // ✅ Approximate base rates against USD (for fallback/demo use only)
    private static final Map<String, Double> BASE_TO_USD = Map.ofEntries(
            Map.entry("USD", 1.0),
            Map.entry("EUR", 1.07),
            Map.entry("INR", 0.012),
            Map.entry("GBP", 1.26),
            Map.entry("JPY", 0.0067),
            Map.entry("AUD", 0.65),
            Map.entry("CAD", 0.73),
            Map.entry("CHF", 1.1),
            Map.entry("CNY", 0.14),
            Map.entry("NZD", 0.59),
            Map.entry("SGD", 0.73),
            Map.entry("ZAR", 0.052),
            Map.entry("BRL", 0.20)
    );

    public CurrencyService(ConversionRateRepository rateRepository, CurrencyBeaconClient beaconClient) {
        this.rateRepository = rateRepository;
        this.beaconClient = beaconClient;
    }

    /**
     * Convert amount from one currency to another.
     */
    @Transactional
    public ConvertResponse convert(String from, String to, Long amount) {
        Double rate = getCachedOrFetchRate(from, to);
        double converted = amount * rate;

        // Save user conversion history (not cache)
        ConversionRate history = new ConversionRate();
        history.setFromCurrency(from);
        history.setToCurrency(to);
        history.setRate(rate);
        history.setAmount(amount);
        history.setConvertedAmount(converted);
        history.setFetchedAt(LocalDateTime.now());
        history.setIsCache(false);

        rateRepository.save(history);

        return new ConvertResponse(from, to, amount, converted, rate);
    }

    /**
     * Return cached rate if not older than 1 hour, otherwise fetch from API or fallback.
     */
    private Double getCachedOrFetchRate(String from, String to) {
        return rateRepository.findCachedRate(from, to)
                .filter(r -> ChronoUnit.HOURS.between(r.getFetchedAt(), LocalDateTime.now()) < 1)
                .map(cached -> {
                    log.info("✅ Using cached rate for {} -> {}", from, to);
                    return cached.getRate();
                })
                .orElseGet(() -> {
                    log.info("🌐 Fetching new rate for {} -> {}", from, to);
                    return fetchAndCacheRate(from, to);
                });
    }

    /**
     * Fetch rate from external API.
     * If API fails, fallback to cross-rate via USD.
     */
    @Transactional
    protected Double fetchAndCacheRate(String from, String to) {
        try {
            Double rate = beaconClient.getConversionRate(from, to);
            if (rate == null) {
                throw new IllegalStateException("API returned null rate");
            }

            cacheRate(from, to, rate);
            return rate;
        } catch (Exception e) {
            log.error("❌ Failed to fetch rate for {} -> {}: {}. Using fallback via USD.", from, to, e.getMessage());

            // ✅ Fallback cross-rate via USD
            Double fromUsd = BASE_TO_USD.getOrDefault(from, 1.0);
            Double toUsd = BASE_TO_USD.getOrDefault(to, 1.0);
            Double fallback = toUsd / fromUsd;

            cacheRate(from, to, fallback);
            return fallback;
        }
    }

    private void cacheRate(String from, String to, Double rate) {
        rateRepository.deleteCachedRate(from, to);

        ConversionRate cacheEntry = new ConversionRate();
        cacheEntry.setFromCurrency(from);
        cacheEntry.setToCurrency(to);
        cacheEntry.setRate(rate);
        cacheEntry.setFetchedAt(LocalDateTime.now());
        cacheEntry.setIsCache(true);

        rateRepository.save(cacheEntry);
    }

    /**
     * Get user conversion history.
     */
    public List<ConversionRate> getConversionHistory() {
        return rateRepository.findConversionHistory();
    }

    /**
     * Get only currency codes (with fallback).
     */
    public List<String> getAllCurrencyCodes() {
        try {
            Map<String, String> currencies = beaconClient.getAllCurrencies();
            return currencies.keySet().stream().sorted().toList();
        } catch (Exception e) {
            log.warn("⚠️ Falling back to static currency list: {}", e.getMessage());
            return FALLBACK_CURRENCIES;
        }
    }

    /**
     * Get currencies with code + name (with fallback).
     */
    public List<String> getAllCurrencies() {
        try {
            Map<String, String> currencies = beaconClient.getAllCurrencies();
            return currencies.entrySet().stream()
                    .map(entry -> entry.getKey() + " - " + entry.getValue())
                    .sorted()
                    .toList();
        } catch (Exception e) {
            log.warn("⚠️ Falling back to static currency list with names: {}", e.getMessage());
            return FALLBACK_CURRENCIES.stream()
                    .map(code -> code + " - [Static Name]")
                    .toList();
        }
    }
}
