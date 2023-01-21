package com.nulltwenty.ordersaggregation.service.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class PricingServiceImpl implements PricingService {
    private static final String URL = "http://127.0.0.1:4000/pricing?countryCode=";
    @Autowired
    RestTemplate restTemplate;

    @Override
    public ResponseEntity<BigDecimal> getPricing(String countryCode) {
        return restTemplate.exchange(URL + countryCode, HttpMethod.GET, null, new ParameterizedTypeReference<BigDecimal>() {
        });
    }
}
