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
        // Configuración de Refresh Token
        private RefreshToken refreshToken = new RefreshToken();

        // Lista de reglas de rutas
        private List<RouteRule> routes = new ArrayList<>();
    }
    @Data
    public static class RefreshToken {
        private boolean enabled = true; // ¿Queremos usar refresh tokens?
        private long expirationMs = 2592000000L; // Default: 30 días
    }

    @Data
    public static class RateLimit {
        private boolean enabled = true;       // Feature Toggle
        private int maxAttempts = 5;          // Cuántas balas tienes
        private int durationSeconds = 60;     // Cuánto tarda en recargar (Ventana de tiempo)
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
        // Regla básica universal (casi siempre necesaria)
        private int minLength = 8;

        // Reglas booleanas (Opcionales, para configuración rápida)
        private boolean requireUppercase = false; // Las apagamos por defecto si prefieres
        private boolean requireNumbers = false;

        // --- LA MEJORA: Patrón Personalizado ---
        // Si el usuario define esto, tiene control total.
        // Ej: "^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" (Letras y números, min 8)
        private String regexp = null;

        // El mensaje que se mostrará si la Regex falla
        private String regexpErrorMessage = "La contraseña no cumple con los requisitos de seguridad.";
    }
}
