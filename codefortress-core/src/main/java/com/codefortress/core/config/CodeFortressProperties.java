package com.codefortress.core.config;

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

    @Data
    public static class Security {
        private String jwtSecret = "default-super-secret-key-please-change-me-in-production-environment"; // Valor por defecto seguro (length)
        private long jwtExpirationMs = 86400000; // 1 día

        // Lista de reglas de rutas
        private List<RouteRule> routes = new ArrayList<>();
    }

    @Data
    public static class Cors {
        private boolean enabled = false; // Desactivado por defecto por seguridad
        private List<String> allowedOrigins = Collections.singletonList("*"); // Ojo con esto en prod
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = Collections.singletonList("*");
        private boolean allowCredentials = true;
    }

    @Data
    public static class Api {
        private boolean enabled = true; // Por defecto UI activada
        private String authPath = "/auth";
    }

    @Data
    public static class RouteRule {
        private String pattern; // Ej: "/api/admin/**"
        private List<String> roles; // Ej: ["ADMIN", "MANAGER"] o ["PUBLIC"]
    }
    @Data
    public static class Password {
        private int minLength = 8;
        private boolean requireUppercase = true;
        private boolean requireLowercase = true;
        private boolean requireNumbers = true;
        private boolean requireSpecialChar = false;
    }
}
