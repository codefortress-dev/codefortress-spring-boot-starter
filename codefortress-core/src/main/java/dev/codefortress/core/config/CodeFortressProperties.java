package dev.codefortress.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration properties for the CodeFortress library.
 * This class maps to the {@code codefortress} prefix in the application's configuration files.
 */
@Data
@ConfigurationProperties(prefix = "codefortress")
public class CodeFortressProperties {

    /**
     * Security-related properties.
     */
    private Security security = new Security();

    /**
     * API-related properties.
     */
    private Api api = new Api();

    /**
     * CORS-related properties.
     */
    private Cors cors = new Cors();

    /**
     * Password policy properties.
     */
    private Password password = new Password();

    /**
     * Rate limiting properties.
     */
    private RateLimit rateLimit = new RateLimit();

    /**
     * Security-related properties.
     */
    @Data
    public static class Security {
        /**
         * The secret key for signing JWTs.
         */
        private String jwtSecret = "default-super-secret-key-please-change-me-in-production-environment";

        /**
         * The expiration time for JWTs in milliseconds.
         */
        private long jwtExpirationMs = 900000; // 15 minutes

        /**
         * Refresh token properties.
         */
        private RefreshToken refreshToken = new RefreshToken();

        /**
         * A list of route rules for securing endpoints.
         */
        private List<RouteRule> routes = new ArrayList<>();
    }

    /**
     * Refresh token properties.
     */
    @Data
    public static class RefreshToken {
        /**
         * Whether refresh tokens are enabled.
         */
        private boolean enabled = true;

        /**
         * The expiration time for refresh tokens in milliseconds.
         */
        private long expirationMs = 2592000000L; // 30 days

        /**
         * The maximum number of active sessions per user.
         */
        private int maxSessions = 1;
    }

    /**
     * Rate limiting properties.
     */
    @Data
    public static class RateLimit {
        /**
         * Whether rate limiting is enabled.
         */
        private boolean enabled = true;

        /**
         * The maximum number of attempts allowed.
         */
        private int maxAttempts = 5;

        /**
         * The duration in seconds for the rate limit.
         */
        private int durationSeconds = 60;
    }

    /**
     * CORS properties.
     */
    @Data
    public static class Cors {
        /**
         * Whether CORS is enabled.
         */
        private boolean enabled = false;

        /**
         * The allowed origins for CORS requests.
         */
        private List<String> allowedOrigins = Collections.singletonList("*");

        /**
         * The allowed HTTP methods for CORS requests.
         */
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

        /**
         * The allowed headers for CORS requests.
         */
        private List<String> allowedHeaders = Collections.singletonList("*");

        /**
         * Whether to allow credentials in CORS requests.
         */
        private boolean allowCredentials = true;
    }

    /**
     * API properties.
     */
    @Data
    public static class Api {
        /**
         * Whether the API is enabled.
         */
        private boolean enabled = true;

        /**
         * The base path for the authentication API.
         */
        private String authPath = "/auth";
    }

    /**
     * A rule for securing a route.
     */
    @Data
    public static class RouteRule {
        /**
         * The URL pattern to match.
         */
        private String pattern;

        /**
         * The roles required to access the route.
         */
        private List<String> roles;
    }

    /**
     * Password policy properties.
     */
    @Data
    public static class Password {
        /**
         * The minimum length of a password.
         */
        private int minLength = 8;

        /**
         * Whether a password must contain at least one uppercase letter.
         */
        private boolean requireUppercase = false;

        /**
         * Whether a password must contain at least one number.
         */
        private boolean requireNumbers = false;

        /**
         * A regular expression that the password must match.
         */
        private String regexp = null;

        /**
         * The error message to display when the password does not match the regular expression.
         */
        private String regexpErrorMessage = "La contraseña no cumple con los requisitos de seguridad.";
    }
}
