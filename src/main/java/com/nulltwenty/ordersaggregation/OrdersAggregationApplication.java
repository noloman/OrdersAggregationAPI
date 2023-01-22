package com.nulltwenty.ordersaggregation;

import com.nulltwenty.ordersaggregation.serializer.AggregatedResponseSerializer;
import com.nulltwenty.ordersaggregation.serializer.TrackResponseSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrdersAggregationApplication {
    @Value("${default-connection-timeout}")
    private int timeout;

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
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        return new RestTemplate(requestFactory);
    }
}