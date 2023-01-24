package com.nulltwenty.ordersaggregation.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.ShipmentsResponse;
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
public class ShipmentServiceImplTest {
    @InjectMocks
    private final ShipmentService shipmentService = new ShipmentServiceImpl();
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void givenARestTemplate_whenItReturns200_theServiceShouldReturnAResponseEntityOfAStringArray() throws JsonProcessingException {
        String[] shipmentArray = {"BOX", "BOX", "PALLET"};
        String shipmentString = objectMapper.writeValueAsString(shipmentArray);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(shipmentString, HttpStatus.OK));

        ShipmentsResponse expectedShipmentResponse = new ShipmentsResponse();
        expectedShipmentResponse.put("1", shipmentArray);

        ResponseEntity<String> serviceResponse = shipmentService.getShipmentOrder(new int[]{1});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);

        ShipmentsResponse actualResponse = objectMapper.readValue(serviceResponse.getBody(), ShipmentsResponse.class);
        String expectedStringResponse = objectMapper.writeValueAsString(expectedShipmentResponse);
        String actualStringResponse = objectMapper.writeValueAsString(actualResponse);
        assertEquals(expectedStringResponse, actualStringResponse);
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAResponseEntityWithEmptyCurlyBracketsAnd200OK() {
        String serviceUnavailableResponse = "\"503:\"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        ResponseEntity<String> serviceResponse = shipmentService.getShipmentOrder(new int[]{1});

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals("{}", serviceResponse.getBody());
    }
}