package com.jansmerecki.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deviceApi() {
        return new OpenAPI().info(new Info()
                .title("Network Device API")
                .version("v1")
                .description("API for registering devices, listing, retrieving by MAC, and building topology."));
    }
}