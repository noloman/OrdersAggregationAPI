package com.nulltwenty.ordersaggregation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrdersAggregationController {
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TrackStatusService trackStatusService;
    @Autowired
    private PricingService pricingService;

    @GetMapping(value = "/aggregation")
    public ResponseEntity<String> aggregation(@RequestParam(required = false) int[] shipmentsOrderNumbers, @RequestParam(required = false) int[] trackOrderNumbers, @RequestParam(required = false) String[] pricingCountryCodes) throws IOException {
        ResponseEntity<String> shipmentOrderResponseEntity = shipmentService.getShipmentOrder(shipmentsOrderNumbers);
        ResponseEntity<String> trackStatusResponseEntity = trackStatusService.getTrackStatus(trackOrderNumbers);
        ResponseEntity<String> pricingResponseEntity = pricingService.getPricing(pricingCountryCodes);

        return ResponseEntity.ok().body(createAggregatedResponseAsJson(shipmentOrderResponseEntity, trackStatusResponseEntity, pricingResponseEntity));
    }

    private String createAggregatedResponseAsJson(ResponseEntity<String> shipmentOrderResponseEntity, ResponseEntity<String> trackStatusResponseEntity, ResponseEntity<String> pricingResponseEntity) throws JsonProcessingException {
        AggregatedResponse aggregatedResponse = new AggregatedResponse();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<>() {
        };

        addTrackToAggregatedResponse(trackStatusResponseEntity, aggregatedResponse, mapper, typeReference);
        addShipmentsAggregatedResponse(shipmentOrderResponseEntity, aggregatedResponse, mapper, typeReference);
        addPricingToAggregatedResponse(pricingResponseEntity, aggregatedResponse, mapper, typeReference);
        return mapper.writeValueAsString(aggregatedResponse);
    }

    private void addPricingToAggregatedResponse(ResponseEntity<String> pricingResponseEntity, AggregatedResponse aggregatedResponse, ObjectMapper mapper, TypeReference<HashMap<String, Object>> typeReference) throws JsonProcessingException {
        if (pricingResponseEntity != null && pricingResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(pricingResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setPricing(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addShipmentsAggregatedResponse(ResponseEntity<String> shipmentOrderResponseEntity, AggregatedResponse aggregatedResponse, ObjectMapper mapper, TypeReference<HashMap<String, Object>> typeReference) throws JsonProcessingException {
        if (shipmentOrderResponseEntity != null && shipmentOrderResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(shipmentOrderResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setShipments(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addTrackToAggregatedResponse(ResponseEntity<String> trackStatusResponseEntity, AggregatedResponse aggregatedResponse, ObjectMapper mapper, TypeReference<HashMap<String, Object>> typeReference) throws JsonProcessingException {
        if (trackStatusResponseEntity != null && trackStatusResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(trackStatusResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setTrack(entry.getKey(), entry.getValue());
            }
        }
    }
}
