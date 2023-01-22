package com.nulltwenty.ordersaggregation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
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
import java.util.Arrays;
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
    public ResponseEntity<String> aggregation(@RequestParam(required = false) int[] shipmentsOrderNumbers, @RequestParam(required = false) int[] trackOrderNumbers, @RequestParam(required = false) String[] pricingCountryCodes) throws IOException, JSONException {
        AggregatedResponse aggregatedResponse = new AggregatedResponse();

        String shipmentOrderResponse = getShipmentOrder(shipmentsOrderNumbers).getBody();
        String trackStatusResponseBody = getTrackStatus(trackOrderNumbers).getBody();
        String pricingResponseBody = getPricing(pricingCountryCodes).getBody();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<>() {
        };
        Map<String, Object> data = mapper.readValue(trackStatusResponseBody, typeReference);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            aggregatedResponse.setTrack(entry.getKey(), entry.getValue());
        }

        mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> shipmentOrderResponseTypeReference = new TypeReference<>() {
        };
        Map<String, Object> shipmentOrderResponseData = mapper.readValue(shipmentOrderResponse, shipmentOrderResponseTypeReference);
        for (Map.Entry<String, Object> entry : shipmentOrderResponseData.entrySet()) {
            aggregatedResponse.setShipments(entry.getKey(), entry.getValue());
        }

        mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> pricingResponseTypeReference = new TypeReference<>() {
        };
        Map<String, Object> pricingResponseData = mapper.readValue(pricingResponseBody, pricingResponseTypeReference);
        for (Map.Entry<String, Object> entry : pricingResponseData.entrySet()) {
            aggregatedResponse.setPricing(entry.getKey(), entry.getValue());
        }
        return ResponseEntity.ok().body(new ObjectMapper().writeValueAsString(aggregatedResponse));
    }

    private HttpEntity<String> getPricing(String[] countryCodes) {
        if (countryCodes != null) {
            try {
                Map<String, Double> map = new HashMap<>();
                for (int i = 0; i < countryCodes.length; i++) {
                    String countryCode = countryCodes[i];
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
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < shipmentsOrderNumbers.length; i++) {
                    int shipmentsOrderNumber = shipmentsOrderNumbers[i];
                    String[] response = shipmentService.getShipmentProducts(shipmentsOrderNumber).getBody();
                    map.put(String.valueOf(shipmentsOrderNumber), Arrays.toString(Arrays.stream(response).toArray()));
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

    private ResponseEntity<String> getTrackStatus(int[] trackOrderNumbers) {
        if (trackOrderNumbers != null) {
            try {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < trackOrderNumbers.length; i++) {
                    int trackOrderNumber = trackOrderNumbers[i];
                    String response = trackStatusService.getTrackStatusFromOrderNumber(trackOrderNumber).getBody().replaceAll("\"", "");
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
