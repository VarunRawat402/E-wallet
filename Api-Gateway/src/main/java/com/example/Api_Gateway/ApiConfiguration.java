package com.example.Api_Gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfiguration {
    @Bean
    public RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-route", r -> r
                        .path("/user/**", "/admin/**", "/login/**")
                        .uri("lb://User-service")) // Service name must match spring.application.name
                .route("transaction-service-route", r -> r
                        .path("/txn/**")
                        .uri("lb://Transaction-service")) // Service name must match spring.application.name
                .build();
    }
}
