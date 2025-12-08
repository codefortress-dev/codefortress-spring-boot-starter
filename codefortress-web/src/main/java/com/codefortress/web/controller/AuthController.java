package com.codefortress.web.controller;

import com.codefortress.core.model.CodeFortressUser;
import com.codefortress.core.service.JwtService;
import com.codefortress.core.service.PasswordValidator;
import com.codefortress.core.spi.CodeFortressUserProvider;
import com.codefortress.web.dto.ErrorResponse;
import com.codefortress.web.dto.LoginRequest;
import com.codefortress.web.dto.RegisterRequest;
import com.codefortress.web.dto.TokenResponse;
import com.codefortress.core.event.CodeFortressUserCreatedEvent; // Importar evento
import org.springframework.context.ApplicationEventPublisher;
import com.codefortress.web.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;

@RestController
@RequestMapping("${codefortress.api.auth-path:/auth}") // Ruta configurable, default /auth
@RequiredArgsConstructor
// FEATURE TOGGLE: Si el usuario pone 'false', este controlador desaparece.
@ConditionalOnProperty(prefix = "codefortress.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CodeFortressUserProvider userProvider;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordValidator passwordValidator;
    private final RateLimitService rateLimitService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        // 1. Obtener IP del cliente
        String ip = httpRequest.getRemoteAddr();

        // 2. Verificar Rate Limit
        Bucket bucket = rateLimitService.resolveBucket(ip);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            // 429 Too Many Requests
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse(429, "Too Many Requests",
                            "Has excedido los intentos de login. Intenta en " + waitForRefill + " segundos.",
                            LocalDateTime.now()));
        }
        // 1. Delegamos la autenticación a Spring Security (que usará nuestro UserDetailsService configurado en Fase 5)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // 2. Si pasamos el paso 1, el usuario es válido. Generamos Token.
        // Nota: authentication.getPrincipal() devolverá nuestro CodeFortressUserDetails
        // Pero para generar el token necesitamos el objeto de dominio, lo reconstruimos o buscamos.
        // Por simplicidad, buscamos al usuario para obtener sus roles frescos.
        CodeFortressUser user = userProvider.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User found in context but not in provider? Impossible."));

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<CodeFortressUser> register(@RequestBody RegisterRequest request) {
        passwordValidator.validate(request.password());
        String encodedPassword = passwordEncoder.encode(request.password());

        // 2. Creamos el modelo agnóstico
        CodeFortressUser newUser = new CodeFortressUser(
                request.username(),
                encodedPassword,
                request.roles() != null ? request.roles() : new HashSet<>(),
                true // Enabled por defecto
        );

        CodeFortressUser savedUser = userProvider.save(newUser);


        // Esto es asíncrono/desacoplado por defecto en la lógica del negocio
        eventPublisher.publishEvent(new CodeFortressUserCreatedEvent(savedUser));

        return ResponseEntity.ok(savedUser);
    }
}