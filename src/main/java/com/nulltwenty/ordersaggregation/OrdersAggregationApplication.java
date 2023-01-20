package com.nulltwenty.ordersaggregation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrdersAggregationApplication {
    private static final Logger LOG = LoggerFactory.getLogger(OrdersAggregationApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(OrdersAggregationApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}