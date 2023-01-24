package com.nulltwenty.ordersaggregation.service.shipment;

import com.nulltwenty.ordersaggregation.model.dto.ShipmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentServiceImpl implements ShipmentService {
    private static final String URL = "http://127.0.0.1:4000/shipment-products?orderNumber=";
    private final Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<String[]> getShipmentProducts(int orderNumber) {
        return restTemplate.getForEntity(URL + orderNumber, String[].class);
    }

    @Override
    public List<ShipmentDTO> getShipmentOrder(int[] shipmentsOrderNumbers) {
        List<ShipmentDTO> shipmentList = new ArrayList<>();
        if (shipmentsOrderNumbers != null) {
            try {
                for (int shipmentsOrderNumber : shipmentsOrderNumbers) {
                    ResponseEntity<String[]> responseEntity = getShipmentProducts(shipmentsOrderNumber);
                    if (responseEntity.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        ShipmentDTO shipmentDTO = new ShipmentDTO(String.valueOf(shipmentsOrderNumber), responseEntity.getBody());
                        shipmentList.add(shipmentDTO);
                    } else {
                        logger.error("Service unavailable: {}", responseEntity.getBody());
                    }
                }
            } catch (Exception e) {
                logger.error("Exception: {}", e.getMessage());
            }
        }
        return shipmentList;
    }
}
