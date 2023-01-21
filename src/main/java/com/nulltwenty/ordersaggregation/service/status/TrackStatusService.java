package com.nulltwenty.ordersaggregation.service.status;

import org.springframework.http.ResponseEntity;

public interface TrackStatusService {
    ResponseEntity<String> getTrackStatusFromOrderNumber(int orderNumber);
}
