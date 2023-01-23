package com.nulltwenty.ordersaggregation.service.pricing;

import org.springframework.http.ResponseEntity;

public interface PricingService {
    ResponseEntity<String> getPricing(String[] countryCodes);
}
