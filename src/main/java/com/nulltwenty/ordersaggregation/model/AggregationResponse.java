package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.*;

public class AggregationResponse {
    private Shipments shipments;
    private Track track;
    private Pricing pricing;

    @JsonProperty("shipments")
    public Shipments getShipments() { return shipments; }
    @JsonProperty("shipments")
    public void setShipments(Shipments value) { this.shipments = value; }

    @JsonProperty("track")
    public Track getTrack() { return track; }
    @JsonProperty("track")
    public void setTrack(Track value) { this.track = value; }

    @JsonProperty("pricing")
    public Pricing getPricing() { return pricing; }
    @JsonProperty("pricing")
    public void setPricing(Pricing value) { this.pricing = value; }
}
