package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.*;

public class Track {
    private String the123456789;

    @JsonProperty("123456789")
    public String getThe123456789() { return the123456789; }
    @JsonProperty("123456789")
    public void setThe123456789(String value) { this.the123456789 = value; }
}
