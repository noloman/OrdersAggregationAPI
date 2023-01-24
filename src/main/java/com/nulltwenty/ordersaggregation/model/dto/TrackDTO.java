package com.nulltwenty.ordersaggregation.model.dto;

public class TrackDTO {
    private String status;
    private String trackNumber;

    public TrackDTO() {
    }

    public TrackDTO(String trackNumber, String status) {
        this.status = status;
        this.trackNumber = trackNumber;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
