package com.nulltwenty.ordersaggregation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        ResponseEntity<String> shipmentOrderResponseEntity = getShipmentOrder(shipmentsOrderNumbers);
        ResponseEntity<String> trackStatusResponseEntity = getTrackStatus(trackOrderNumbers);
        ResponseEntity<String> pricingResponseEntity = getPricing(pricingCountryCodes);

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
        if (pricingResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(pricingResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setPricing(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addShipmentsAggregatedResponse(ResponseEntity<String> shipmentOrderResponseEntity, AggregatedResponse aggregatedResponse, ObjectMapper mapper, TypeReference<HashMap<String, Object>> typeReference) throws JsonProcessingException {
        if (shipmentOrderResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(shipmentOrderResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setShipments(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addTrackToAggregatedResponse(ResponseEntity<String> trackStatusResponseEntity, AggregatedResponse aggregatedResponse, ObjectMapper mapper, TypeReference<HashMap<String, Object>> typeReference) throws JsonProcessingException {
        if (trackStatusResponseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
            Map<String, Object> data = mapper.readValue(trackStatusResponseEntity.getBody(), typeReference);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                aggregatedResponse.setTrack(entry.getKey(), entry.getValue());
            }
        }
    }

    private ResponseEntity<String> getPricing(String[] countryCodes) {
        if (countryCodes != null) {
            try {
                Map<String, Double> map = new HashMap<>();
                for (String countryCode : countryCodes) {
                    ResponseEntity<String> responseEntity = pricingService.getPricing(countryCode);
                    if (responseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(countryCode, Double.parseDouble(Objects.requireNonNull(responseEntity.getBody())));
                    } else {
                        return responseEntity;
                    }
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, Double> priceLine : map.entrySet()) {
                    returnValue.put(priceLine.getKey(), priceLine.getValue());
                }
                return ResponseEntity.ok().body(returnValue.toString());
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }

    private ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
        if (shipmentsOrderNumbers != null) {
            try {
                Map<String, String[]> map = new HashMap<>();
                for (int shipmentsOrderNumber : shipmentsOrderNumbers) {
                    ResponseEntity<String[]> response = shipmentService.getShipmentProducts(shipmentsOrderNumber);
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(String.valueOf(shipmentsOrderNumber), response.getBody());
                    } else {
                        return new ResponseEntity<>(Objects.requireNonNull(response.getBody()).toString(), HttpStatus.SERVICE_UNAVAILABLE);
                    }
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, String[]> trackLine : map.entrySet()) {
                    JSONArray shipmentArray = new JSONArray();
                    for (String packagingValue : trackLine.getValue()) {
                        shipmentArray.put(packagingValue);
                    }
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

    private ResponseEntity<String> getTrackStatus(int[] trackOrderNumbers) {
        if (trackOrderNumbers != null) {
            try {
                Map<String, String> map = new HashMap<>();
                for (int trackOrderNumber : trackOrderNumbers) {
                    ResponseEntity<String> response = Objects.requireNonNull(trackStatusService.getTrackStatusFromOrderNumber(trackOrderNumber));
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(String.valueOf(trackOrderNumber), response.getBody());
                    } else {
                        return response;
                    }
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, String> trackLine : map.entrySet()) {
                    returnValue.put(trackLine.getKey(), trackLine.getValue());
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
