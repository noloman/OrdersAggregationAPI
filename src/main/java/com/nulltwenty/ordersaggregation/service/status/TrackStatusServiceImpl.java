package com.nulltwenty.ordersaggregation.service.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TrackStatusServiceImpl implements TrackStatusService {
    private final String URL = "http://127.0.0.1:4000/track-status?orderNumber=";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<String> getTrackStatusFromOrderNumber(int orderNumber) {
        return restTemplate.getForEntity(URL + orderNumber, String.class);
    }

    @Override
    public ResponseEntity<String> getTrackStatus(int[] trackOrderNumbers) {
        if (trackOrderNumbers != null) {
            try {
                Map<String, String> map = new HashMap<>();
                for (int trackOrderNumber : trackOrderNumbers) {
                    ResponseEntity<String> response = Objects.requireNonNull(getTrackStatusFromOrderNumber(trackOrderNumber));
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        map.put(String.valueOf(trackOrderNumber), Objects.requireNonNull(response.getBody()).replaceAll("\"", ""));
                    }
                }
                return ResponseEntity.ok().body(objectMapper.writeValueAsString(map));
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            return ResponseEntity.ok().body(new JSONObject().toString());
        }
    }
}
