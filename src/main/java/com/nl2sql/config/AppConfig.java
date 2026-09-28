package com.nl2sql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate is Spring's HTTP client — we use it to call the Claude API.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}