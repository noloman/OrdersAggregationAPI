package com.nulltwenty.ordersaggregation.service.pricing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PricingServiceImpl implements PricingService {
    public static final String URL = "http://127.0.0.1:4000/pricing?countryCode=";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> getPricing(String countryCode) {
        return restTemplate.getForEntity(URL + countryCode, String.class);
    }

    @Override
    public ResponseEntity<String> getPricing(String[] countryCodes) {
        if (countryCodes != null) {
            try {
                Map<String, Double> map = new HashMap<>();
                for (String countryCode : countryCodes) {
                    ResponseEntity<String> responseEntity = Objects.requireNonNull(getPricing(countryCode));
                    if (responseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(countryCode, Double.valueOf(Objects.requireNonNull(responseEntity.getBody())));
                    }
                }
                return ResponseEntity.ok().body(objectMapper.writeValueAsString(map));
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
