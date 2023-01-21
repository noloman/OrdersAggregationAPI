package com.nulltwenty.ordersaggregation.service.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TrackStatusServiceImpl implements TrackStatusService {
    private final String URL = "http://127.0.0.1:4000/track-status?orderNumber=";

    @Autowired
    RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> getTrackStatusFromOrderNumber(int orderNumber) {
        return restTemplate.exchange(URL + orderNumber, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });
    }
}
