package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class AggregatedResponse {
    private final Map<String, Object> track = new HashMap<>();
    private final Map<String, Object> shipments = new HashMap<>();
    private final Map<String, Object> pricing = new HashMap<>();

    public Map<String, Object> getTrack() {
        return track;
    }

    @JsonAnySetter
    public void setTrack(String name, Object value) {
        track.put(name, value);
    }

    public Map<String, Object> getShipments() {
        return shipments;
    }

    @JsonAnySetter
    public void setShipments(String name, Object value) {
        shipments.put(name, value);
    }

    public Map<String, Object> getPricing() {
        return pricing;
    }

    @JsonAnySetter
    public void setPricing(String name, Object value) {
        pricing.put(name, value);
    }
}
