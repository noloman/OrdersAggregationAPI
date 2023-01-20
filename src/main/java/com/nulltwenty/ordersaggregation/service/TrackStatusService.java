package com.nulltwenty.ordersaggregation.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TrackStatusService {
    private final String URL = "http://127.0.0.1:4000/track-status?orderNumber=";
    private final RestTemplate restTemplate;

    public TrackStatusService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getTrackStatusFromOrderNumber(int[] orderNumberArray) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < orderNumberArray.length; i++) {
            if (i == orderNumberArray.length - 1) {
                strBuilder.append(orderNumberArray[i]);
            } else {
                strBuilder.append(orderNumberArray[i] + ",");
            }
        }
        String orderNumbers = strBuilder.toString();

        ResponseEntity<String> exchange = restTemplate.exchange(URL + orderNumbers, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });
        return exchange;
    }
}
