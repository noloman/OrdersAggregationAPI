package com.nulltwenty.ordersaggregation.service.pricing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.PricingResponse;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class PricingServiceImplTest {
    @InjectMocks
    private final PricingService pricingService = new PricingServiceImpl();
    private final String countryCode = "NL";
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void givenARestTemplate_whenItReturns200OK_theServiceShouldReturnAResponseEntityOfAStringArray() throws JsonProcessingException {
        PricingResponse pricingResponse = new PricingResponse();
        pricingResponse.put(countryCode, Double.parseDouble("12.0102356"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String pricingResponseString = objectMapper.writeValueAsString(pricingResponse);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>("12.0102356", HttpStatus.OK));

        ResponseEntity<String> serviceResponse = pricingService.getPricing(new String[]{countryCode});
        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(pricingResponseString, serviceResponse.getBody());
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAnEmptyResponseWith200OK() {
        String serviceUnavailableResponse = "\"503:\"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        ResponseEntity<String> serviceResponse = pricingService.getPricing(new String[]{countryCode});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals("{}", serviceResponse.getBody());
    }
}