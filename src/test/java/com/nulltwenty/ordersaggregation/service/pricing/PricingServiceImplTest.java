package com.nulltwenty.ordersaggregation.service.pricing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nulltwenty.ordersaggregation.model.PricingResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class PricingServiceImplTest {
    private final PricingServiceImpl pricingService = new PricingServiceImpl();
    private final String countryCode = "NL";
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        pricingService.setRestTemplate(restTemplate);
    }

    @Test
    public void givenARestTemplate_whenItReturns200OK_theServiceShouldReturnTheSame() throws JsonProcessingException {
        PricingResponse pricingResponse = new PricingResponse();
        pricingResponse.setPrice(BigDecimal.TEN);
        pricingResponse.setCountryCode(countryCode);

        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(pricingResponse, HttpStatus.OK));

        ResponseEntity<String> serviceResponse = pricingService.getPricing(countryCode);
        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(pricingResponse, serviceResponse.getBody());
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnTheSame() {
        String serviceUnavailableResponse = "\"503 : \"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        ResponseEntity<String> serviceResponse = pricingService.getPricing(countryCode);

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.SERVICE_UNAVAILABLE);
        assertEquals(serviceUnavailableResponse, serviceResponse.getBody());
    }
}