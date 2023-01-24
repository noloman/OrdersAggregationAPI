package com.nulltwenty.ordersaggregation.service.status;

import com.nulltwenty.ordersaggregation.model.dto.TrackDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TrackStatusServiceImpl implements TrackStatusService {
    private final String URL = "http://127.0.0.1:4000/track-status?orderNumber=";
    private final Logger logger = LoggerFactory.getLogger(TrackStatusServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> getTrackStatusFromOrderNumber(int orderNumber) {
        return restTemplate.getForEntity(URL + orderNumber, String.class);
    }

    @Override
    public List<TrackDTO> getTrackStatus(int[] trackOrderNumbers) {
        List<TrackDTO> trackList = new ArrayList<>();
        if (trackOrderNumbers != null) {
            try {
                for (int trackOrderNumber : trackOrderNumbers) {
                    ResponseEntity<String> response = Objects.requireNonNull(getTrackStatusFromOrderNumber(trackOrderNumber));
                    if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE) {
                        TrackDTO trackDTO = new TrackDTO(String.valueOf(trackOrderNumber), Objects.requireNonNull(response.getBody()).replaceAll("\"", ""));
                        trackList.add(trackDTO);
                    } else {
                        logger.error("Service unavailable: {}", response.getBody());
                    }
                }
            } catch (Exception e) {
                logger.error("Service unavailable: {}", e.getMessage());
            }
        }
        return trackList;
    }
}
