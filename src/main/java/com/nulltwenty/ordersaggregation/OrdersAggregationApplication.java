package com.nulltwenty.ordersaggregation;

import com.nulltwenty.ordersaggregation.serializer.AggregatedResponseSerializer;
import com.nulltwenty.ordersaggregation.serializer.TrackResponseSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrdersAggregationApplication {
    private final int DEFAULT_TIMEOUT = 5000;

    public static void main(String[] args) {
        SpringApplication.run(OrdersAggregationApplication.class, args);
    }

    @Bean
    TrackResponseSerializer trackResponseSerializer() {
        return new TrackResponseSerializer();
    }

    @Bean
    AggregatedResponseSerializer aggregatedResponseSerializer() {
        return new AggregatedResponseSerializer();
    }

    @Bean
    RestTemplate restTemplateTimeoutWithRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(DEFAULT_TIMEOUT);
        requestFactory.setReadTimeout(DEFAULT_TIMEOUT);
        return new RestTemplate(requestFactory);
    }
}