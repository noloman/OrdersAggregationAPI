package com.nulltwenty.ordersaggregation.service.shipment;

import org.springframework.http.ResponseEntity;

public interface ShipmentService {
    ResponseEntity<String> getShipmentProducts(int[] orderNumberArray);
}