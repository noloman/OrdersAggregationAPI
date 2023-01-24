package com.nulltwenty.ordersaggregation.service.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.dto.TrackDTO;
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
public class TrackStatusServiceTest {
    @InjectMocks
    private TrackStatusServiceImpl trackStatusService;
    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    public void givenARestTemplate_whenItReturns200_theServiceShouldReturnAResponseEntityOfAString() throws JsonProcessingException {
        List<TrackDTO> expectedServerResponse = new ArrayList<>();
        TrackDTO trackDTO = new TrackDTO();
        trackDTO.setTrackNumber(String.valueOf(1));
        String fakeServerResponse = "COLLECTING";
        trackDTO.setStatus(fakeServerResponse);
        expectedServerResponse.add(trackDTO);

        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(fakeServerResponse, HttpStatus.OK));

        List<TrackDTO> actualServerResponse = trackStatusService.getTrackStatus(new int[]{1});
        Assertions.assertNotNull(actualServerResponse);

        assertEquals(objectMapper.writeValueAsString(expectedServerResponse), objectMapper.writeValueAsString(actualServerResponse));
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAResponseEntityOfEmptyCurlyBracketsAnd200OK() throws JsonProcessingException {
        String serviceUnavailableResponse = "\"503:\"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        List<TrackDTO> serviceResponse = trackStatusService.getTrackStatus(new int[]{1});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(objectMapper.writeValueAsString(new ArrayList<>()), objectMapper.writeValueAsString(serviceResponse));
    }
}