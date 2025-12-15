package dev.codefortress.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codefortress.web.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * An {@link AuthenticationEntryPoint} that handles unauthorized access attempts.
 * It returns a JSON error response with a 401 Unauthorized status.
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Commences an authentication scheme.
     *
     * @param request       the request during which an AuthenticationException is thrown
     * @param response      the response
     * @param authException the exception that was thrown
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Acceso denegado: Token inválido o ausente",
                LocalDateTime.now()
        );

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
