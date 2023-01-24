package com.nulltwenty.ordersaggregation.service.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class TrackStatusServiceTest {
    @InjectMocks
    private final TrackStatusService trackStatusService = new TrackStatusServiceImpl();
    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    public void givenARestTemplate_whenItReturns200_theServiceShouldReturnAResponseEntityOfAString() throws JsonProcessingException {
        String trackString = "COLLECTING";
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(trackString, HttpStatus.OK));

        ResponseEntity<String> serviceResponse = trackStatusService.getTrackStatus(new int[]{1});
        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);

        Map<String, String> response = new HashMap<>(2);
        response.put("1", trackString);
        String expected = objectMapper.writeValueAsString(response);
        assertEquals(expected, serviceResponse.getBody());
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAResponseEntityOfEmptyCurlyBracketsAnd200OK() {
        String serviceUnavailableResponse = "\"503:\"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        ResponseEntity<String> serviceResponse = trackStatusService.getTrackStatus(new int[]{1});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals("{}", serviceResponse.getBody());
    }
}