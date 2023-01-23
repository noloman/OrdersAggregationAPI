package com.nulltwenty.ordersaggregation.service.pricing;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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


    private ResponseEntity<String> getPricing(String countryCode) {
        return restTemplate.exchange(URL + countryCode, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public ResponseEntity<String> getPricing(String[] countryCodes) {
        if (countryCodes != null) {
            try {
                Map<String, Double> map = new HashMap<>();
                for (String countryCode : countryCodes) {
                    ResponseEntity<String> responseEntity = getPricing(countryCode);
                    if (responseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(countryCode, Double.valueOf(Objects.requireNonNull(responseEntity.getBody())));
                    }
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, Double> priceLine : map.entrySet()) {
                    returnValue.put(priceLine.getKey(), priceLine.getValue());
                }
                return ResponseEntity.ok().body(returnValue.toString());
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
