package com.nulltwenty.ordersaggregation.model;

import java.util.Map;

public class AggregatedResponse {
    private Map<String, String[]> shipments;
    private Map<String, String> track;
    private Map<String, Double> pricing;

    public Map<String, String[]> getShipments() {
        return shipments;
    }

    public void setShipments(Map<String, String[]> shipments) {
        this.shipments = shipments;
    }

    public Map<String, String> getTrack() {
        return track;
    }

    public void setTrack(Map<String, String> track) {
        this.track = track;
    }

    public Map<String, Double> getPricing() {
        return pricing;
    }

    public void setPricing(Map<String, Double> pricing) {
        this.pricing = pricing;
    }
}
