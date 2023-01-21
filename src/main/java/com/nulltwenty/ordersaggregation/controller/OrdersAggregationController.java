package com.nulltwenty.ordersaggregation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;
import com.nulltwenty.ordersaggregation.model.TrackResponse;
import com.nulltwenty.ordersaggregation.serializer.TrackResponseSerializer;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(OrdersAggregationController.class);
    private final ShipmentService shipmentService;
    private final TrackStatusService trackStatusService;

    public OrdersAggregationController(ShipmentService shipmentService, TrackStatusService trackStatusService) {
        this.shipmentService = shipmentService;
        this.trackStatusService = trackStatusService;
    }

    @GetMapping(value = "/aggregation")
    public ResponseEntity<String> aggregation(@RequestParam int[] shipmentsOrderNumbers, @RequestParam int[] trackOrderNumbers, @RequestParam String[] pricingCountryCodes) throws IOException {
        LOG.debug("Input", shipmentsOrderNumbers.toString(), shipmentsOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        AggregatedResponse aggregatedResponse = new AggregatedResponse();

        String shipmentOrderResponse = getShipmentOrder(shipmentsOrderNumbers).getBody();
        String trackStatusResponseBody = getTrackStatus(shipmentsOrderNumbers).getBody();

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
        return ResponseEntity.ok().body(new ObjectMapper().writeValueAsString(aggregatedResponse));
    }

    private ResponseEntity<String> getShipmentOrder(int[] shipmentsOrderNumbers) {
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
    }

    private ResponseEntity<String> getTrackStatus(int[] trackOrderNumbers) {
        try {
            TrackResponse trackResponse = new TrackResponse();
            for (int i = 0; i < trackOrderNumbers.length - 1; i++) {
                int trackOrderNumber = trackOrderNumbers[i];
                String response = trackStatusService.getTrackStatusFromOrderNumber(trackOrderNumber).getBody();
                trackResponse.number = String.valueOf(trackOrderNumber);
                trackResponse.value = response;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(TrackResponse.class, new TrackResponseSerializer());
            objectMapper.registerModule(module);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(trackResponse));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(503));
        }
    }
}
