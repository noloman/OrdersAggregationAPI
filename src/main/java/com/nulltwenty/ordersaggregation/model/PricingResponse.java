package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.*;

public class PricingResponse {
    private double nl;
    private double cn;

    @JsonProperty("NL")
    public double getNl() { return nl; }
    @JsonProperty("NL")
    public void setNl(double value) { this.nl = value; }

    @JsonProperty("CN")
    public double getCN() { return cn; }
    @JsonProperty("CN")
    public void setCN(double value) { this.cn = value; }
}
