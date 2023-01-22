package com.nulltwenty.ordersaggregation;

import com.nulltwenty.ordersaggregation.serializer.AggregatedResponseSerializer;
import com.nulltwenty.ordersaggregation.serializer.TrackResponseSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrdersAggregationApplication {
    private final int DEFAULT_TIMEOUT = 5000;

    public static void main(String[] args) {
        SpringApplication.run(OrdersAggregationApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
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
    ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(DEFAULT_TIMEOUT);
        clientHttpRequestFactory.setConnectionRequestTimeout(DEFAULT_TIMEOUT);
        return clientHttpRequestFactory;
    }
}