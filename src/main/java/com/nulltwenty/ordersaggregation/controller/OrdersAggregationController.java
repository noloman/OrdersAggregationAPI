package com.nulltwenty.ordersaggregation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<String> aggregation(@RequestParam(required = false) int[] shipmentsOrderNumbers, @RequestParam(required = false) int[] trackOrderNumbers, @RequestParam(required = false) String[] pricingCountryCodes) throws IOException, JSONException {
        String shipmentOrderResponse = getShipmentOrder(shipmentsOrderNumbers).getBody();
        String trackStatusResponseBody = getTrackStatus(trackOrderNumbers).getBody();
        String pricingResponseBody = getPricing(pricingCountryCodes).getBody();

        return ResponseEntity.ok().body(createAggregatedResponseAsJson(shipmentOrderResponse, trackStatusResponseBody, pricingResponseBody));
    }

    private String createAggregatedResponseAsJson(String shipmentOrderResponse, String trackStatusResponseBody, String pricingResponseBody) throws JsonProcessingException {
        AggregatedResponse aggregatedResponse = new AggregatedResponse();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<>() {
        };

        Map<String, Object> data = mapper.readValue(trackStatusResponseBody, typeReference);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            aggregatedResponse.setTrack(entry.getKey(), entry.getValue());
        }

        data = mapper.readValue(shipmentOrderResponse, typeReference);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            aggregatedResponse.setShipments(entry.getKey(), entry.getValue());
        }

        data = mapper.readValue(pricingResponseBody, typeReference);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            aggregatedResponse.setPricing(entry.getKey(), entry.getValue());
        }
        return mapper.writeValueAsString(aggregatedResponse);
    }

    private HttpEntity<String> getPricing(String[] countryCodes) {
        if (countryCodes != null) {
            try {
                Map<String, Double> map = new HashMap<>();
                for (String countryCode : countryCodes) {
                    String price = pricingService.getPricing(countryCode).getBody();
                    map.put(countryCode, Double.parseDouble(price));
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, Double> priceLine : map.entrySet()) {
                    returnValue.put(priceLine.getKey(), priceLine.getValue());
                }
                return ResponseEntity.ok().body(returnValue.toString());
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
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
                    String[] response = shipmentService.getShipmentProducts(shipmentsOrderNumber).getBody();
                    map.put(String.valueOf(shipmentsOrderNumber), response);
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
                return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
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
                    String response = Objects.requireNonNull(trackStatusService.getTrackStatusFromOrderNumber(trackOrderNumber).getBody()).replaceAll("\"", "");
                    map.put(String.valueOf(trackOrderNumber), response);
                }
                JSONObject returnValue = new JSONObject();
                for (Map.Entry<String, String> trackLine : map.entrySet()) {
                    returnValue.put(trackLine.getKey(), trackLine.getValue());
                }
                return ResponseEntity.ok().body(returnValue.toString());
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
