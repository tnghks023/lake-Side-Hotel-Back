package com.shwan.lakeSidehotel.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMappingConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // All paths
                .allowedOrigins("http://localhost:5173") // Origins allowed to access
                .allowedMethods("GET", "POST", "PUT", "DELETE") // HTTP methods allowed
                .allowedHeaders("*") // Headers allowed in the request
                .allowCredentials(true) // Credentials allowed
                .maxAge(3600); // Max age of the CORS request
    }
}
