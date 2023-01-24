package com.nulltwenty.ordersaggregation.service.pricing;

import com.nulltwenty.ordersaggregation.model.dto.PricingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PricingServiceImpl implements PricingService {
    public static final String URL = "http://127.0.0.1:4000/pricing?countryCode=";

    private final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<Double> getPricing(String countryCode) {
        return restTemplate.getForEntity(URL + countryCode, Double.class);
    }

    @Override
    public List<PricingDTO> getPricing(String[] countryCodes) {
        List<PricingDTO> pricingList = new ArrayList<>();
        if (countryCodes != null) {
            try {
                for (String countryCode : countryCodes) {
                    ResponseEntity<Double> responseEntity = Objects.requireNonNull(getPricing(countryCode));
                    if (responseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        PricingDTO pricingDTO = new PricingDTO(countryCode, Objects.requireNonNull(responseEntity.getBody()));
                        pricingList.add(pricingDTO);
                    } else {
                        logger.error("Service unavailable: {}", responseEntity.getBody());
                    }
                }
            } catch (Exception e) {
                logger.error("Exception: {}", e.getMessage());
            }
        }
        return pricingList;
    }
}
