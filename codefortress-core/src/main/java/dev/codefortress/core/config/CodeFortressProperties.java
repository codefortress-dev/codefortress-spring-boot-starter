package dev.codefortress.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "codefortress")
public class CodeFortressProperties {

    private Security security = new Security();
    private Api api = new Api();
    private Cors cors = new Cors();
    private Password password = new Password();
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class Security {
        private String jwtSecret = "default-super-secret-key-please-change-me-in-production-environment"; // Valor por defecto seguro (length)
        private long jwtExpirationMs = 900000; // 1 día

        private RefreshToken refreshToken = new RefreshToken();

        private List<RouteRule> routes = new ArrayList<>();
    }
    @Data
    public static class RefreshToken {
        private boolean enabled = true;
        private long expirationMs = 2592000000L;
        private int maxSessions = 1;
    }

    @Data
    public static class RateLimit {
        private boolean enabled = true;
        private int maxAttempts = 5;
        private int durationSeconds = 60;
    }

    @Data
    public static class Cors {
        private boolean enabled = false;
        private List<String> allowedOrigins = Collections.singletonList("*");
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = Collections.singletonList("*");
        private boolean allowCredentials = true;
    }


    @Data
    public static class Api {
        private boolean enabled = true;
        private String authPath = "/auth";
    }

    @Data
    public static class RouteRule {
        private String pattern;
        private List<String> roles;
    }
    @Data
    public static class Password {

        private int minLength = 8;

        private boolean requireUppercase = false;
        private boolean requireNumbers = false;

        private String regexp = null;

        private String regexpErrorMessage = "La contraseña no cumple con los requisitos de seguridad.";
    }
}
