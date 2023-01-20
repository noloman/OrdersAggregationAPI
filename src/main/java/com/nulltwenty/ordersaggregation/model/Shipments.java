package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Shipments {
    private List<String> the987654321;

    @JsonProperty("987654321")
    public List<String> getThe987654321() { return the987654321; }
    @JsonProperty("987654321")
    public void setThe987654321(List<String> value) { this.the987654321 = value; }
}
