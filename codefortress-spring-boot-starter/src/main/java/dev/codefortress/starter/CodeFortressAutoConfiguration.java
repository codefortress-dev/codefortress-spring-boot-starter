package dev.codefortress.starter;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.security.CodeFortressUserDetails;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import dev.codefortress.core.spi.CodeFortressUserProvider;
import dev.codefortress.jpa.adapter.JpaRefreshTokenProvider;
import dev.codefortress.jpa.adapter.JpaUserProvider;
import dev.codefortress.jpa.repository.RefreshTokenRepository;
import dev.codefortress.jpa.repository.SecurityRoleRepository;
import dev.codefortress.jpa.repository.SecurityUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import dev.codefortress.starter.config.JwtAuthenticationEntryPoint;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import dev.codefortress.starter.audit.LoggerAuditProvider;
import dev.codefortress.starter.audit.CodeFortressAuditListener;
import dev.codefortress.starter.support.CodeFortressLifecycleLogger;
@Configuration
@EnableConfigurationProperties(CodeFortressProperties.class)
@ComponentScan(basePackages = { "dev.codefortress.core", "dev.codefortress.web" })

public class CodeFortressAutoConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(prefix = "codefortress.security.refresh-token", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(CodeFortressRefreshTokenProvider.class)
    public CodeFortressRefreshTokenProvider defaultRefreshTokenProvider(
            RefreshTokenRepository tokenRepo,
            SecurityUserRepository userRepo,
            CodeFortressProperties properties) {
        return new JpaRefreshTokenProvider(tokenRepo, userRepo, properties);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(CodeFortressUserProvider userProvider) {
        return username -> userProvider.findByUsername(username)
                .map(CodeFortressUserDetails::new)
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


    @Configuration
    @ConditionalOnClass(dev.codefortress.jpa.adapter.JpaUserProvider.class)
    @EnableJpaRepositories(basePackages = "dev.codefortress.jpa.repository")
    @EntityScan(basePackages = "dev.codefortress.jpa.entity")
    @ConditionalOnProperty(prefix = "codefortress.data", name = "type", havingValue = "jpa", matchIfMissing = true)
    static class JpaAdapterConfiguration {

        @Bean
        @ConditionalOnMissingBean(CodeFortressUserProvider.class)
        public CodeFortressUserProvider defaultJpaProvider(SecurityUserRepository repo, SecurityRoleRepository roleRepository) {
            return new JpaUserProvider(repo, roleRepository);
        }
    }

    @Bean
    public CodeFortressLifecycleLogger codeFortressLifecycleLogger() {
        return new CodeFortressLifecycleLogger();
    }
}