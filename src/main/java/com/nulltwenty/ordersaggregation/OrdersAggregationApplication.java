package com.nulltwenty.ordersaggregation;

import com.nulltwenty.ordersaggregation.serializer.AggregatedResponseSerializer;
import com.nulltwenty.ordersaggregation.serializer.TrackResponseSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrdersAggregationApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersAggregationApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    TrackResponseSerializer trackResponseSerializer() {
        return new TrackResponseSerializer();
    }

    @Bean
    AggregatedResponseSerializer aggregatedResponseSerializer() {
        return new AggregatedResponseSerializer();
    }
}