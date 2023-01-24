package com.nulltwenty.ordersaggregation.service.pricing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.dto.PricingDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class PricingServiceImplTest {
    private final String countryCode = "NL";
    @InjectMocks
    private PricingServiceImpl pricingService;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void givenARestTemplate_whenItReturns200OK_theServiceShouldReturnAResponseEntityOfAStringArray() throws JsonProcessingException {
        Double fakeServerResponse = 12.0102356;
        PricingDTO pricingDTO = new PricingDTO(countryCode, fakeServerResponse);
        List<PricingDTO> expectedServerResponse = new ArrayList<>();
        expectedServerResponse.add(pricingDTO);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Double.class))).thenReturn(new ResponseEntity<>(fakeServerResponse, HttpStatus.OK));

        List<PricingDTO> actualServerResponse = pricingService.getPricing(new String[]{countryCode});

        Assertions.assertNotNull(actualServerResponse);
        assertEquals(objectMapper.writeValueAsString(expectedServerResponse), objectMapper.writeValueAsString(actualServerResponse));
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAnEmptyResponseWith200OK() throws JsonProcessingException {
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Double.class))).thenReturn(new ResponseEntity<>(0.0, HttpStatus.SERVICE_UNAVAILABLE));

        List<PricingDTO> serviceResponse = pricingService.getPricing(new String[]{countryCode});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(objectMapper.writeValueAsString(new ArrayList<>()), objectMapper.writeValueAsString(serviceResponse));
    }
}