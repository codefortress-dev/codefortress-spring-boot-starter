package com.codefortress.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "codefortress")
public class CodeFortressProperties {

    private Security security = new Security();
    private Api api = new Api();

    @Data
    public static class Security {
        private String jwtSecret = "default-super-secret-key-please-change-me-in-production-environment"; // Valor por defecto seguro (length)
        private long jwtExpirationMs = 86400000; // 1 día

        // Lista de reglas de rutas
        private List<RouteRule> routes = new ArrayList<>();
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
}
