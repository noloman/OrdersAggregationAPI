package com.nulltwenty.ordersaggregation.service.shipment;

import org.json.JSONArray;
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
    private RestTemplate restTemplate;


    private ResponseEntity<String> getShipmentProducts(int orderNumber) {
        return restTemplate.getForEntity(URL + orderNumber, String.class);
    }

    @Override
    public ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
        if (shipmentsOrderNumbers != null) {
            try {
                Map<String, String> map = new HashMap<>();
                 for (int shipmentsOrderNumber : shipmentsOrderNumbers) {
                    ResponseEntity<String> response = getShipmentProducts(shipmentsOrderNumber);
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(String.valueOf(shipmentsOrderNumber), response.getBody());
                    }
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, String> trackLine : map.entrySet()) {
                    JSONArray shipmentArray = new JSONArray();
                    shipmentArray.put(trackLine.getValue());
                    returnValue.put(trackLine.getKey(), shipmentArray);
                }
                return ResponseEntity.ok().body(returnValue.toString());
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
