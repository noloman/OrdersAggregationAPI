package com.nulltwenty.ordersaggregation.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nulltwenty.ordersaggregation.model.dto.ShipmentDTO;

import java.util.List;

public interface ShipmentService {
    List<ShipmentDTO> getShipmentOrder(int[] shipmentsOrderNumbers) throws JsonProcessingException;
}