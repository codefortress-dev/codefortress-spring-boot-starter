package com.codefortress.web.controller;

import com.codefortress.core.model.CodeFortressUser;
import com.codefortress.core.service.JwtService;
import com.codefortress.core.spi.CodeFortressUserProvider;
import com.codefortress.web.dto.LoginRequest;
import com.codefortress.web.dto.RegisterRequest;
import com.codefortress.web.dto.TokenResponse;
import com.codefortress.core.event.CodeFortressUserCreatedEvent; // Importar evento
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
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
        // 1. Hasheamos la contraseña ANTES de pasarla al provider/base de datos
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