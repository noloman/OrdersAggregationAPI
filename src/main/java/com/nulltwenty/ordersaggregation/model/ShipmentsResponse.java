package com.nulltwenty.ordersaggregation.model;

import java.util.List;

public class ShipmentsResponse {
    private String number;
    private List<String> shipmentStatus;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<String> getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(List<String> shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }
}
