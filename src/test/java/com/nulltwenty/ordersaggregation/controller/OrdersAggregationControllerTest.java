package com.nulltwenty.ordersaggregation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OrdersAggregationControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void givenAnAggregationEndPoint_whenTheRequestHasNoParams_itShouldReturnAnEmptyAggregationResponseJson() {
        webClient.get().uri("/aggregation").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("{\"track\":{},\"shipments\":{},\"pricing\":{}}");
    }
}