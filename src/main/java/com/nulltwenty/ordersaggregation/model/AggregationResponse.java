package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.*;

public class AggregationResponse {
    private ShipmentsResponse shipmentsResponse;
    private TrackResponse trackResponse;
    private PricingResponse pricingResponse;

    @JsonProperty("shipments")
    public ShipmentsResponse getShipments() { return shipmentsResponse; }
    @JsonProperty("shipments")
    public void setShipmentsResponse(ShipmentsResponse value) { this.shipmentsResponse = value; }

    @JsonProperty("track")
    public TrackResponse getTrackResponse() { return trackResponse; }
    @JsonProperty("track")
    public void setTrackResponse(TrackResponse value) { this.trackResponse = value; }

    @JsonProperty("pricing")
    public PricingResponse getPricing() { return pricingResponse; }
    @JsonProperty("pricing")
    public void setPricing(PricingResponse value) { this.pricingResponse = value; }
}
