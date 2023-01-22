package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class OrdersAggregationControllerTest {
    private MockMvc mvc;
    @InjectMocks
    private OrdersAggregationController ordersAggregationController;
    @Mock
    private ShipmentService shipmentService;
    @Mock
    private TrackStatusService trackStatusService;
    @Mock
    private PricingService pricingService;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(ordersAggregationController).build();
    }

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoParams_itShouldReturnAnEmptyAggregationResponseJson() throws Exception {
        String expectedResponse = "{\"track\":{},\"shipments\":{},\"pricing\":{}}";
        MockHttpServletResponse response = mvc.perform(get("/aggregation").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertNotNull(response);
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoCountryCode_itShouldReturnAnEmptyPricingAggregationResponseJson() throws Exception {
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(123456789))).thenReturn(ResponseEntity.ok("COLLECTING"));
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(987654321))).thenReturn(ResponseEntity.ok("DELIVERING"));
        when(shipmentService.getShipmentProducts(eq(987654321))).thenReturn(ResponseEntity.ok(new String[]{"BOX", "BOX", "PALLET"}));
        when(shipmentService.getShipmentProducts(eq(123456789))).thenReturn(ResponseEntity.ok(new String[]{"ENVELOP", "BOX", "ENVELOP", "PALLET"}));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?shipmentsOrderNumbers=987654321,123456789&trackOrderNumbers=987654321,123456789").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"track\":{\"123456789\":\"COLLECTING\",\"987654321\":\"DELIVERING\"},\"shipments\":{\"123456789\":[\"ENVELOP\",\"BOX\",\"ENVELOP\",\"PALLET\"],\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"pricing\":{}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoShipmentsOrderNumber_itShouldReturnAnEmptyShipmentsAggregationResponseJson() throws Exception {
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(123456789))).thenReturn(ResponseEntity.ok("COLLECTING"));
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(987654321))).thenReturn(ResponseEntity.ok("DELIVERING"));
        when(pricingService.getPricing(eq("NL"))).thenReturn(ResponseEntity.ok("20.388832257336986"));
        when(pricingService.getPricing(eq("CN"))).thenReturn(ResponseEntity.ok("5.552640023717359"));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=987654321,123456789&&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"track\":{\"123456789\":\"COLLECTING\",\"987654321\":\"DELIVERING\"},\"shipments\":{},\"pricing\":{\"CN\":5.552640023717359,\"NL\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheTrackStatusServiceRespondsWithAnError_itShouldReturnAnEmptyTrackAggregationResponseJson() throws Exception {
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(123456789))).thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("\"503 : \"{\"message\":\"service unavailable\"}"));
        when(shipmentService.getShipmentProducts(eq(987654321))).thenReturn(ResponseEntity.ok(new String[]{"BOX", "BOX", "PALLET"}));
        when(pricingService.getPricing(eq("NL"))).thenReturn(ResponseEntity.ok("20.388832257336986"));
        when(pricingService.getPricing(eq("CN"))).thenReturn(ResponseEntity.ok("5.552640023717359"));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"track\":{},\"shipments\":{\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"pricing\":{\"CN\":5.552640023717359,\"NL\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheShipmentServiceRespondsWithAnError_itShouldReturnAnEmptyShipmentsAggregationResponseJson() throws Exception {
        when(trackStatusService.getTrackStatusFromOrderNumber(eq(123456789))).thenReturn(ResponseEntity.ok("COLLECTING"));
        when(shipmentService.getShipmentProducts(eq(987654321))).thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new String[]{"\"503 : \"{\"message\":\"service unavailable\"}"}));
        when(pricingService.getPricing(eq("NL"))).thenReturn(ResponseEntity.ok("20.388832257336986"));
        when(pricingService.getPricing(eq("CN"))).thenReturn(ResponseEntity.ok("5.552640023717359"));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"track\":{\"123456789\":\"COLLECTING\"},\"shipments\":{},\"pricing\":{\"CN\":5.552640023717359,\"NL\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }
}