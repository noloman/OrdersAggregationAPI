package com.nulltwenty.ordersaggregation.controller;

import com.nulltwenty.ordersaggregation.model.dto.PricingDTO;
import com.nulltwenty.ordersaggregation.model.dto.ShipmentDTO;
import com.nulltwenty.ordersaggregation.model.dto.TrackDTO;
import com.nulltwenty.ordersaggregation.service.pricing.PricingService;
import com.nulltwenty.ordersaggregation.service.shipment.ShipmentService;
import com.nulltwenty.ordersaggregation.service.status.TrackStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        String expectedResponse = "{\"shipments\":{},\"track\":{},\"pricing\":{}}";
        MockHttpServletResponse response = mvc.perform(get("/aggregation").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertNotNull(response);
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoCountryCode_itShouldReturnAnEmptyPricingAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{987654321, 123456789}))).thenReturn(new ArrayList<>(Arrays.asList(new TrackDTO("123456789", "COLLECTING"), new TrackDTO("987654321", "DELIVERING"))));
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{987654321, 123456789}))).thenReturn(new ArrayList<>(Arrays.asList(new ShipmentDTO("123456789", new String[]{"ENVELOP", "BOX", "ENVELOP", "PALLET"}), new ShipmentDTO("987654321", new String[]{"BOX", "BOX", "PALLET"}))));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?shipmentsOrderNumbers=987654321,123456789&trackOrderNumbers=987654321,123456789").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{\"123456789\":[\"ENVELOP\",\"BOX\",\"ENVELOP\",\"PALLET\"],\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"track\":{\"123456789\":\"COLLECTING\",\"987654321\":\"DELIVERING\"},\"pricing\":{}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoShipmentsOrderNumber_itShouldReturnAnEmptyShipmentsAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{987654321, 123456789}))).thenReturn(List.of(new TrackDTO("123456789", "COLLECTING"), new TrackDTO("987654321", "DELIVERING")));
        Mockito.when(pricingService.getPricing(eq(new String[]{"NL", "CN"}))).thenReturn(List.of(new PricingDTO("CN", 5.552640023717359), new PricingDTO("CN", 5.552640023717359), new PricingDTO("NL", 20.388832257336986)));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=987654321,123456789&&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{},\"track\":{\"123456789\":\"COLLECTING\",\"987654321\":\"DELIVERING\"},\"pricing\":{\"CN\":5.552640023717359,\"NL\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheTrackStatusServiceRespondsWithAnError_itShouldReturnAnEmptyTrackAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{123456789}))).thenReturn(new ArrayList<>());
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{987654321}))).thenReturn(List.of(new ShipmentDTO("987654321", new String[]{"BOX", "BOX", "PALLET"})));
        Mockito.when(pricingService.getPricing(eq(new String[]{"NL", "CN"}))).thenReturn(List.of(new PricingDTO("CN", 5.552640023717359), new PricingDTO("NL", 20.388832257336986)));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"track\":{},\"pricing\":{\"CN\":5.552640023717359,\"NL\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheShipmentServiceRespondsWithAnErrorForOneOfTheOrderNumbers_itShouldShipTheSuccessfulOrderInTheAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{123456789}))).thenReturn(List.of(new TrackDTO("123456789", "COLLECTING")));
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{123456789, 987654321}))).thenReturn(List.of(new ShipmentDTO("987654321", new String[]{"BOX", "BOX", "PALLET"})));
        Mockito.when(pricingService.getPricing(eq(new String[]{"NL", "CN"}))).thenReturn(List.of(new PricingDTO("CN", 20.388832257336986)));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=123456789,987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"track\":{\"123456789\":\"COLLECTING\"},\"pricing\":{\"CN\":20.388832257336986}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheTrackStatusServiceRespondsWithAnErrorForOneOfTheOrders_itShouldTrackTheSuccessfulOrderInTheAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{123456789, 987654321}))).thenReturn(List.of(new TrackDTO("987654321", "COLLECTING")));
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{987654321}))).thenReturn(List.of(new ShipmentDTO("987654321", new String[]{"BOX", "BOX", "PALLET"})));
        Mockito.when(pricingService.getPricing(eq(new String[]{"NL", "CN"}))).thenReturn(List.of(new PricingDTO("CN", 78.13363484600944), new PricingDTO("NL", 18.528027571519413)));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789,987654321&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"track\":{\"987654321\":\"COLLECTING\"},\"pricing\":{\"CN\":78.13363484600944,\"NL\":18.528027571519413}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenTheShipmentServiceRespondsWithAnError_itShouldReturnAnEmptyShipmentsAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{123456789}))).thenReturn(List.of(new TrackDTO("123456789", "COLLECTING")));
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{987654321}))).thenReturn(new ArrayList<>());
        Mockito.when(pricingService.getPricing(eq(new String[]{"NL", "CN"}))).thenReturn(List.of(new PricingDTO("CN", 78.13363484600944), new PricingDTO("NL", 18.528027571519413)));

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{},\"track\":{\"123456789\":\"COLLECTING\"},\"pricing\":{\"CN\":78.13363484600944,\"NL\":18.528027571519413}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void givenAnAggregationEndPoint_whenThePricingServiceRespondsWithAnError_itShouldReturnAnEmptyPricingAggregationResponseJson() throws Exception {
        Mockito.when(trackStatusService.getTrackStatus(eq(new int[]{123456789}))).thenReturn(List.of(new TrackDTO("123456789", "COLLECTING")));
        Mockito.when(shipmentService.getShipmentOrder(eq(new int[]{987654321}))).thenReturn(List.of(new ShipmentDTO("987654321", new String[]{"BOX", "BOX", "PALLET"})));
        Mockito.when(pricingService.getPricing(any())).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mvc.perform(get("/aggregation?trackOrderNumbers=123456789&shipmentsOrderNumbers=987654321&pricingCountryCodes=NL,CN").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertNotNull(response);

        String expectedResponse = "{\"shipments\":{\"987654321\":[\"BOX\",\"BOX\",\"PALLET\"]},\"track\":{\"123456789\":\"COLLECTING\"},\"pricing\":{}}";
        assertEquals(expectedResponse, response.getContentAsString());
    }
}