package com.nulltwenty.ordersaggregation.service.status;

import com.nulltwenty.ordersaggregation.model.dto.TrackDTO;

import java.util.List;

public interface TrackStatusService {
    List<TrackDTO> getTrackStatus(int[] trackOrderNumbers);
}
