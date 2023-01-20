package com.nulltwenty.ordersaggregation.model;

public class TrackResponse {
    private String number;
    private String value;

    public TrackResponse(String number, String value) {
        this.number = number;
        this.value = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
