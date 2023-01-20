package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.model.AggregationResponse;
import com.nulltwenty.ordersaggregation.model.ShipmentsResponse;
import com.nulltwenty.ordersaggregation.model.TrackingStatusResponse;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OrdersAggregationController {
    private static final Logger LOG = LoggerFactory.getLogger(OrdersAggregationController.class);
    private final ShipmentService shipmentService;
    private final TrackStatusService trackStatusService;

    public OrdersAggregationController(ShipmentService shipmentService, TrackStatusService trackStatusService) {
        this.shipmentService = shipmentService;
        this.trackStatusService = trackStatusService;
    }

    @GetMapping(value = "/aggregation")
    public ResponseEntity<AggregationResponse> aggregation(@RequestParam int[] shipmentsOrderNumbers, @RequestParam int[] trackOrderNumbers, @RequestParam String[] pricingCountryCodes) throws IOException {
        LOG.debug("Input", shipmentsOrderNumbers.toString(), shipmentsOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        String[] shipmentOrderResponse = new String[]{getShipmentOrder(shipmentsOrderNumbers).getBody().replaceAll("\"", "")};
        String trackStatusResponseBody = getTrackStatus(shipmentsOrderNumbers).getBody().replaceAll("\"", "");
        TrackingStatusResponse trackingStatusResponse = TrackingStatusResponse.valueOf(trackStatusResponseBody);

        AggregationResponse response = new AggregationResponse();
        return ResponseEntity.ok().body(response);
    }

    private ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
        try {
            ShipmentsResponse shipmentsResponse = new ShipmentsResponse();
            List<String> shipmentStatusList = new ArrayList<>();
            for (int i = 0; i < shipmentsOrderNumbers.length - 1; i++) {
                int shipmentsOrderNumber = shipmentsOrderNumbers[i];
                String response = shipmentService.getShipmentProducts(shipmentsOrderNumber).getBody();
                shipmentsResponse.setNumber(String.valueOf(shipmentsOrderNumber));
                shipmentStatusList.add(response);
            }
            shipmentsResponse.setShipmentStatus(shipmentStatusList);
            return ResponseEntity.ok().body(shipmentsResponse.toString());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
        }
    }

    private ResponseEntity<String> getTrackStatus(int[] shipmentsOrderNumbers) {
        try {
            return trackStatusService.getTrackStatusFromOrderNumber(shipmentsOrderNumbers);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
        }
    }
}
