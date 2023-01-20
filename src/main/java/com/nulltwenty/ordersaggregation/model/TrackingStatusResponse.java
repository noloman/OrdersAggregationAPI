package com.nulltwenty.ordersaggregation.model;

public enum TrackingStatusResponse {
    NEW("NEW"), IN_TRANSIT("IN_TRANSIT"), COLLECTING("COLLECTING"), COLLECTED("COLLECTED"), DELIVERING("DELIVERING"), DELIVERED("DELIVERED");

    private final String text;

    TrackingStatusResponse(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
