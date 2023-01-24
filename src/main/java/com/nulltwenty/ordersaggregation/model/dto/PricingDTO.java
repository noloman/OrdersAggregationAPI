package com.nulltwenty.ordersaggregation.model.dto;

public class PricingDTO {
    private String countryCode;
    private Double price;

    public PricingDTO(String countryCode, Double price) {
        this.countryCode = countryCode;
        this.price = price;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
