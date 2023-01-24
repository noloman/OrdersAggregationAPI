package com.nulltwenty.ordersaggregation.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.dto.ShipmentDTO;
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
public class ShipmentServiceImplTest {
    @InjectMocks
    private ShipmentServiceImpl shipmentService;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void givenARestTemplate_whenItReturns200_theServiceShouldReturnAResponseEntityOfAStringArray() throws JsonProcessingException {
        String[] fakeServerResponse = new String[]{"BOX", "BOX", "PALLET"};
        ShipmentDTO shipmentDTO = new ShipmentDTO("1", fakeServerResponse);
        List<ShipmentDTO> expectedServerResponse = new ArrayList<>();
        expectedServerResponse.add(shipmentDTO);

        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String[].class))).thenReturn(new ResponseEntity<>(fakeServerResponse, HttpStatus.OK));

        List<ShipmentDTO> actualServerResponse = shipmentService.getShipmentOrder(new int[]{1});

        Assertions.assertNotNull(actualServerResponse);
        assertEquals(objectMapper.writeValueAsString(expectedServerResponse), objectMapper.writeValueAsString(actualServerResponse));
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAResponseEntityWithEmptyCurlyBracketsAnd200OK() throws JsonProcessingException {
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String[].class))).thenReturn(new ResponseEntity<>(new String[]{}, HttpStatus.SERVICE_UNAVAILABLE));

        List<ShipmentDTO> serviceResponse = shipmentService.getShipmentOrder(new int[]{1});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(objectMapper.writeValueAsString(new ArrayList<>()), objectMapper.writeValueAsString(serviceResponse));
    }
}