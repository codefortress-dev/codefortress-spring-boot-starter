package com.codefortress.starter.config;

import com.codefortress.core.config.CodeFortressProperties;
import com.codefortress.starter.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CodeFortressSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CodeFortressProperties properties; // Inyectamos configuración del usuario

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // 1. Permitir rutas de login configuradas
                    auth.requestMatchers(properties.getUi().getLoginPath()).permitAll();
                    auth.requestMatchers("/auth/**").permitAll(); // Fallback

                    // 2. Aplicar reglas dinámicas desde application.yml
                    properties.getSecurity().getRoutes().forEach(rule -> {
                        if (rule.getRoles().contains("PUBLIC")) {
                            auth.requestMatchers(rule.getPattern()).permitAll();
                        } else {
                            auth.requestMatchers(rule.getPattern())
                                    .hasAnyRole(rule.getRoles().toArray(new String[0]));
                        }
                    });

                    // 3. Bloquear todo lo demás por defecto
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}