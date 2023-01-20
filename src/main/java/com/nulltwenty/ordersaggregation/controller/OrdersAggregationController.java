package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.service.ShipmentProductsService;
import com.nulltwenty.ordersaggregation.service.TrackStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OrdersAggregationController {
    private static final Logger LOG = LoggerFactory.getLogger(OrdersAggregationController.class);
    private final ShipmentProductsService shipmentProductsService;
    private final TrackStatusService trackStatusService;

    public OrdersAggregationController(ShipmentProductsService shipmentProductsService, TrackStatusService trackStatusService) {
        this.shipmentProductsService = shipmentProductsService;
        this.trackStatusService = trackStatusService;
    }

    @GetMapping(value = "/aggregation")
    public ResponseEntity<String> aggregation(@RequestParam int[] shipmentsOrderNumbers, @RequestParam int[] trackOrderNumbers, @RequestParam String[] pricingCountryCodes) throws IOException {
        String shipmentOrderResponse = (String) getShipmentOrder(shipmentsOrderNumbers).getBody();
        String trackStatusResponse = (String) getTrackStatus(shipmentsOrderNumbers).getBody();
        return ResponseEntity.ok().body("");
    }

    private ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
        try {
            return shipmentProductsService.getShipmentProductsFromOrderNumber(shipmentsOrderNumbers);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    private ResponseEntity<String> getTrackStatus(int[] shipmentsOrderNumbers) {
        try {
            return trackStatusService.getTrackStatusFromOrderNumber(shipmentsOrderNumbers);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }
}
