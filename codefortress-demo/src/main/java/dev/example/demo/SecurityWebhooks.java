package dev.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityWebhooks {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Gana a CodeFortress (+10)
    public SecurityFilterChain webhookSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/webhooks/**") // Solo atrapa estas URLs
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // O valida la firma HMAC aquí
        return http.build();
    }

}
