package com.nulltwenty.ordersaggregation.service.pricing;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface PricingService {
    ResponseEntity<BigDecimal> getPricing(String countryCode);
}
