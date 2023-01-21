package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class AggregatedResponse {
    private final Map<String, Object> track = new HashMap<>();
    private final Map<String, Object> shipments = new HashMap<>();
    private PricingResponse pricing;

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

    public PricingResponse getPricing() {
        return pricing;
    }

    public void setPricing(PricingResponse value) {
        this.pricing = value;
    }
}
