package com.nulltwenty.ordersaggregation.model;

import java.math.BigDecimal;

public class PricingResponse {
    private String countryCode;
    private BigDecimal price;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
