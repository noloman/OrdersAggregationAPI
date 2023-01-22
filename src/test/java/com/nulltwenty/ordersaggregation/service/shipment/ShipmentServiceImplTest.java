package com.nulltwenty.ordersaggregation.service.shipment;

import com.nulltwenty.ordersaggregation.model.ShipmentsResponse;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceImplTest {
    @InjectMocks
    private final ShipmentService shipmentService = new ShipmentServiceImpl();
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void givenARestTemplate_whenItReturns200_theServiceShouldReturnAResponseEntityOfAStringArray() {
        ShipmentsResponse shipmentsResponse = new ShipmentsResponse();
        ArrayList<String> shipments = new ArrayList<>();
        shipments.add("DELIVERING");
        shipmentsResponse.setShipmentStatus(shipments);
        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(shipmentsResponse, HttpStatus.OK));

        ResponseEntity<String[]> serviceResponse = shipmentService.getShipmentProducts(1);
        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(shipmentsResponse, serviceResponse.getBody());
    }

    @Test
    public void givenARestTemplate_whenItReturns503ServiceUnavailable_theServiceShouldReturnAResponseEntityOfAStringArrayWithAnError() {
        String serviceUnavailableResponse = "\"503 : \"{\"message\":\"service unavailable\"}";
        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(serviceUnavailableResponse, HttpStatus.SERVICE_UNAVAILABLE));

        ResponseEntity<String[]> serviceResponse = shipmentService.getShipmentProducts(1);

        Assertions.assertNotNull(serviceResponse);
        assertEquals(serviceResponse.getStatusCode(), HttpStatus.SERVICE_UNAVAILABLE);
        assertEquals(serviceUnavailableResponse, serviceResponse.getBody());
    }
}