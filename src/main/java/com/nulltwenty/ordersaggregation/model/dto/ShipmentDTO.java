package com.nulltwenty.ordersaggregation.model.dto;

public class ShipmentDTO {
    private String[] packaging;
    private String number;

    public ShipmentDTO(String number, String[] packaging) {
        this.packaging = packaging;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String[] getPackaging() {
        return packaging;
    }

    public void setPackaging(String[] packaging) {
        this.packaging = packaging;
    }
}
