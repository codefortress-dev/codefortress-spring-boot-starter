package dev.codefortress.starter.config;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.starter.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration for the CodeFortress starter.
 * This class configures the security filter chain, CORS, and JWT authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CodeFortressSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CodeFortressProperties properties;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Configures the security filter chain.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    String authBase = properties.getApi().getAuthPath();
                    auth.requestMatchers(authBase + "/**").permitAll();

                    properties.getSecurity().getRoutes().forEach(rule -> {
                        if (rule.getRoles().contains("PUBLIC")) {
                            auth.requestMatchers(rule.getPattern()).permitAll();
                        } else {
                            auth.requestMatchers(rule.getPattern())
                                    .hasAnyRole(rule.getRoles().toArray(new String[0]));
                        }
                    });

                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the CORS settings.
     *
     * @return the configured {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CodeFortressProperties.Cors corsProps = properties.getCors();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        if (corsProps.isEnabled()) {
            config.setAllowedOrigins(corsProps.getAllowedOrigins());
            config.setAllowedMethods(corsProps.getAllowedMethods());
            config.setAllowedHeaders(corsProps.getAllowedHeaders());
            config.setAllowCredentials(corsProps.isAllowCredentials());
        }

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
