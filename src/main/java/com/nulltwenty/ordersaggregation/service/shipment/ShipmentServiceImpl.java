package com.nulltwenty.ordersaggregation.service.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShipmentServiceImpl implements ShipmentService {
    private static final String URL = "http://127.0.0.1:4000/shipment-products?orderNumber=";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<String> getShipmentProducts(int orderNumber) {
        return restTemplate.getForEntity(URL + orderNumber, String.class);
    }

    @Override
    public ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
        if (shipmentsOrderNumbers != null) {
            try {
                Map<String, String[]> map = new HashMap<>();
                for (int shipmentsOrderNumber : shipmentsOrderNumbers) {
                    ResponseEntity<String> response = getShipmentProducts(shipmentsOrderNumber);
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        String[] responseBodyStringArray = objectMapper.readValue(response.getBody(), String[].class);
                        map.put(String.valueOf(shipmentsOrderNumber), responseBodyStringArray);
                    }
                }
                return ResponseEntity.ok().body(objectMapper.writeValueAsString(map));
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
