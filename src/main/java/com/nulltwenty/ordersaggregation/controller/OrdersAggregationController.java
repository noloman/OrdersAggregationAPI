package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.service.ShipmentProductsService;
import com.nulltwenty.ordersaggregation.service.TrackStatusService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OrdersAggregationController {
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

    private ResponseEntity getShipmentOrder(int[] shipmentsOrderNumbers) {
        try {
            ResponseEntity<String> result = shipmentProductsService.getShipmentProductsFromOrderNumber(shipmentsOrderNumbers);
            return result;
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    private ResponseEntity getTrackStatus(int[] shipmentsOrderNumbers) {
        try {
            ResponseEntity<String> result = trackStatusService.getTrackStatusFromOrderNumber(shipmentsOrderNumbers);
            return result;
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }
}
