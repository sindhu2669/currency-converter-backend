package com.example.currency_convertor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversion_rate")
public class ConversionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_currency", nullable = false)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false)
    private String toCurrency;

    @Column(nullable = false)
    private Double rate;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "converted_amount")
    private Double convertedAmount;

    // Flag to identify cache entries vs conversion history
    @Column(name = "is_cache", nullable = false)
    private Boolean isCache = false;

    public ConversionRate() {}

    public ConversionRate(String fromCurrency, String toCurrency, Double rate, LocalDateTime fetchedAt) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
        this.isCache = true;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public Double getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(Double convertedAmount) { this.convertedAmount = convertedAmount; }

    public Boolean getIsCache() { return isCache; }
    public void setIsCache(Boolean isCache) { this.isCache = isCache; }
}