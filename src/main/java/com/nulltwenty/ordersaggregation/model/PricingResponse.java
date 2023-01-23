package com.nulltwenty.ordersaggregation.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PricingResponse {
    private Map<String, Double> values = new HashMap<>();

    @JsonAnySetter
    public void put(String key, Double value) {
        values = Collections.singletonMap(key, value);
    }

    @JsonAnyGetter
    public Map<String, Double> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
