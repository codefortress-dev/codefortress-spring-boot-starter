package dev.codefortress.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.security.CodeFortressUserDetails;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import dev.codefortress.core.spi.CodeFortressUserProvider;
import dev.codefortress.jpa.adapter.JpaRefreshTokenProvider;
import dev.codefortress.jpa.adapter.JpaUserProvider;
import dev.codefortress.jpa.repository.RefreshTokenRepository;
import dev.codefortress.jpa.repository.SecurityRoleRepository;
import dev.codefortress.jpa.repository.SecurityUserRepository;
import dev.codefortress.starter.audit.CodeFortressAuditListener;
import dev.codefortress.starter.audit.LoggerAuditProvider;
import dev.codefortress.starter.config.JwtAuthenticationEntryPoint;
import dev.codefortress.starter.support.CodeFortressLifecycleLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

/**
 * Auto-configuration class for the CodeFortress starter.
 * This class automatically configures the necessary beans for the CodeFortress security framework
 * to integrate with a Spring Boot application.
 */
@Configuration
@EnableConfigurationProperties(CodeFortressProperties.class)
@ComponentScan(basePackages = {"dev.codefortress.core", "dev.codefortress.web"})
public class CodeFortressAutoConfiguration {

    /**
     * Provides a default {@link PasswordEncoder} bean.
     *
     * @return a {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a default {@link CodeFortressRefreshTokenProvider} if one is not already defined.
     * This bean is only created if refresh tokens are enabled in the properties.
     *
     * @param tokenRepo  the refresh token repository
     * @param userRepo   the user repository
     * @param properties the CodeFortress properties
     * @return a {@link JpaRefreshTokenProvider} instance.
     */
    @Bean
    @ConditionalOnProperty(prefix = "codefortress.security.refresh-token", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(CodeFortressRefreshTokenProvider.class)
    public CodeFortressRefreshTokenProvider defaultRefreshTokenProvider(
            RefreshTokenRepository tokenRepo,
            SecurityUserRepository userRepo,
            CodeFortressProperties properties) {
        return new JpaRefreshTokenProvider(tokenRepo, userRepo, properties);
    }

    /**
     * Provides a {@link JwtAuthenticationEntryPoint} to handle unauthorized access.
     *
     * @param objectMapper the object mapper for JSON serialization
     * @return a {@link JwtAuthenticationEntryPoint} instance.
     */
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    /**
     * Provides the {@link AuthenticationManager} bean.
     *
     * @param config the authentication configuration
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a {@link UserDetailsService} that integrates with the {@link CodeFortressUserProvider}.
     *
     * @param userProvider the user provider SPI implementation
     * @return a {@link UserDetailsService} instance.
     */
    @Bean
    public UserDetailsService userDetailsService(CodeFortressUserProvider userProvider) {
        return username -> userProvider.findByUsername(username)
                .map(CodeFortressUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Provides an {@link AuthenticationProvider} that uses the configured {@link UserDetailsService} and {@link PasswordEncoder}.
     *
     * @param userDetailsService the user details service
     * @param passwordEncoder    the password encoder
     * @return a {@link DaoAuthenticationProvider} instance.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Provides a default {@link CodeFortressAuditProvider} that logs to the console if one is not already defined.
     *
     * @return a {@link LoggerAuditProvider} instance.
     */
    @Bean
    @ConditionalOnMissingBean(CodeFortressAuditProvider.class)
    public CodeFortressAuditProvider auditProvider() {
        return new LoggerAuditProvider();
    }

    /**
     * Provides a {@link CodeFortressAuditListener} to listen for and log security events.
     *
     * @param provider the audit provider
     * @return a {@link CodeFortressAuditListener} instance.
     */
    @Bean
    public CodeFortressAuditListener auditListener(CodeFortressAuditProvider provider) {
        return new CodeFortressAuditListener(provider);
    }

    /**
     * Provides a {@link CodeFortressLifecycleLogger} to log a banner on application startup.
     *
     * @return a {@link CodeFortressLifecycleLogger} instance.
     */
    @Bean
    public CodeFortressLifecycleLogger codeFortressLifecycleLogger() {
        return new CodeFortressLifecycleLogger();
    }

    /**
     * Configuration for the JPA adapter.
     * This class is only enabled if the JPA adapter is on the classpath and the data type is configured as 'jpa'.
     */
    @Configuration
    @ConditionalOnClass(dev.codefortress.jpa.adapter.JpaUserProvider.class)
    @EnableJpaRepositories(basePackages = "dev.codefortress.jpa.repository")
    @EntityScan(basePackages = "dev.codefortress.jpa.entity")
    @ConditionalOnProperty(prefix = "codefortress.data", name = "type", havingValue = "jpa", matchIfMissing = true)
    static class JpaAdapterConfiguration {

        /**
         * Provides a default {@link CodeFortressUserProvider} for JPA if one is not already defined.
         *
         * @param repo           the user repository
         * @param roleRepository the role repository
         * @return a {@link JpaUserProvider} instance.
         */
        @Bean
        @ConditionalOnMissingBean(CodeFortressUserProvider.class)
        public CodeFortressUserProvider defaultJpaProvider(SecurityUserRepository repo, SecurityRoleRepository roleRepository) {
            return new JpaUserProvider(repo, roleRepository);
        }
    }
}
