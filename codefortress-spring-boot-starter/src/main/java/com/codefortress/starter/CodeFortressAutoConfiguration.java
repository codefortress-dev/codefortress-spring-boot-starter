package com.codefortress.starter;

import com.codefortress.core.config.CodeFortressProperties;
import com.codefortress.core.security.CodeFortressUserDetails;
import com.codefortress.core.spi.CodeFortressUserProvider;
import com.codefortress.jpa.adapter.JpaUserProvider;
import com.codefortress.jpa.repository.SecurityUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.codefortress.starter.config.JwtAuthenticationEntryPoint;
import com.codefortress.core.audit.CodeFortressAuditProvider;
import com.codefortress.starter.audit.LoggerAuditProvider;
import com.codefortress.starter.audit.CodeFortressAuditListener;

@Configuration
@EnableConfigurationProperties(CodeFortressProperties.class)
@ComponentScan(basePackages = { "com.codefortress.core", "com.codefortress.web" })
// NOTA: No escaneamos "com.codefortress.jpa" aquí automáticamente. Lo hacemos abajo condicionalmente.
public class CodeFortressAutoConfiguration {

    // --- BLOQUE 1: Configuración Básica de Seguridad ---

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        // Le pasamos el mapper de Spring al constructor
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // --- BLOQUE 2: El Adaptador UserDetailsService ---
    // Conecta nuestro SPI (CodeFortressUserProvider) con Spring Security
    @Bean
    public UserDetailsService userDetailsService(CodeFortressUserProvider userProvider) {
        return username -> userProvider.findByUsername(username)
                .map(CodeFortressUserDetails::new) // Adaptador UserDetails
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    @ConditionalOnMissingBean(CodeFortressAuditProvider.class)
    public CodeFortressAuditProvider auditProvider() {
        return new LoggerAuditProvider();
    }

    @Bean
    public CodeFortressAuditListener auditListener(CodeFortressAuditProvider provider) {
        return new CodeFortressAuditListener(provider);
    }

    // --- BLOQUE JPA CONDICIONAL CORREGIDO ---
    @Configuration
    @ConditionalOnClass(com.codefortress.jpa.adapter.JpaUserProvider.class) // Chequea si la clase existe en el classpath
    @EnableJpaRepositories(basePackages = "com.codefortress.jpa.repository") // Activa los repositorios
    @EntityScan(basePackages = "com.codefortress.jpa.entity") // Activa las entidades
    static class JpaAdapterConfiguration {

        @Bean
        @ConditionalOnMissingBean(CodeFortressUserProvider.class) // SOLO si el usuario no creó el suyo
        public CodeFortressUserProvider defaultJpaProvider(SecurityUserRepository repo) {
            // AQUÍ creamos el bean manualmente.
            // Spring inyectará 'repo' porque @EnableJpaRepositories ya lo habrá creado.
            return new JpaUserProvider(repo);
        }
    }
}