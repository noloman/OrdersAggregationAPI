package com.nulltwenty.ordersaggregation.model;

import java.util.List;

public class ShipmentsResponse {
    private String number;
    private List<String> shipmentStatus;

    public String getNumber() {
        return number;
    }

    public List<String> getShipmentStatus() {
        return shipmentStatus;
    }
}
