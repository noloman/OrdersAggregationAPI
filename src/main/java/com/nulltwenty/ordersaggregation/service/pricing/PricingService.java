package com.nulltwenty.ordersaggregation.service.pricing;

import com.nulltwenty.ordersaggregation.model.dto.PricingDTO;

import java.util.List;

public interface PricingService {
    List<PricingDTO> getPricing(String[] countryCodes);
}
