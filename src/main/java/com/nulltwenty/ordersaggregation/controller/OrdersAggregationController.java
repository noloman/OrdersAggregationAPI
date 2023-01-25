package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.model.dto.PricingDTO;
import com.nulltwenty.ordersaggregation.model.dto.ShipmentDTO;
import com.nulltwenty.ordersaggregation.model.dto.TrackDTO;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OrdersAggregationController {
    private final Logger logger = LoggerFactory.getLogger(OrdersAggregationController.class);
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TrackStatusService trackStatusService;
    @Autowired
    private PricingService pricingService;

    @GetMapping(value = "/aggregation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AggregatedResponse> aggregation(@RequestParam(required = false) int[] shipmentsOrderNumbers, @RequestParam(required = false) int[] trackOrderNumbers, @RequestParam(required = false) String[] pricingCountryCodes) throws IOException {

        List<ShipmentDTO> shipmentOrderList = shipmentService.getShipmentOrder(shipmentsOrderNumbers);
        List<TrackDTO> trackStatusOrderList = trackStatusService.getTrackStatus(trackOrderNumbers);
        List<PricingDTO> pricingOrderList = pricingService.getPricing(pricingCountryCodes);

        return ResponseEntity.ok().body(createAggregatedResponseAsJson(shipmentOrderList, trackStatusOrderList, pricingOrderList));
    }

    private AggregatedResponse createAggregatedResponseAsJson(List<ShipmentDTO> shipmentList, List<TrackDTO> trackList, List<PricingDTO> pricingList) {
        AggregatedResponse aggregatedResponse = new AggregatedResponse();

        Map<String, String[]> shipmentsMap = new HashMap<>();
        for (ShipmentDTO shipmentDTO : shipmentList) {
            shipmentsMap.put(shipmentDTO.getNumber(), shipmentDTO.getPackaging());
        }
        aggregatedResponse.setShipments(shipmentsMap);

        Map<String, String> trackMap = new HashMap<>();
        for (TrackDTO trackDTO : trackList) {
            trackMap.put(trackDTO.getTrackNumber(), trackDTO.getStatus());
        }
        aggregatedResponse.setTrack(trackMap);

        Map<String, Double> pricingMap = new HashMap<>();
        for (PricingDTO pricingDTO : pricingList) {
            pricingMap.put(pricingDTO.getCountryCode(), pricingDTO.getPrice());
        }
        aggregatedResponse.setPricing(pricingMap);
        return aggregatedResponse;
    }
}
