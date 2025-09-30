package com.example.currency_convertor.model.dto;

public class ConvertResponse {
    private String fromCurrency;
    private String toCurrency;
    private Long amount;
    private Double convertedAmount;
    private Double rate;

    public ConvertResponse(String fromCurrency, String toCurrency, Long amount, Double convertedAmount, Double rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.rate = rate;
    }

    // Getters
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public Long getAmount() { return amount; }
    public Double getConvertedAmount() { return convertedAmount; }
    public Double getRate() { return rate; }
}
