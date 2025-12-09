package dev.codefortress.web.controller;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.model.CodeFortressRefreshToken;
import dev.codefortress.core.model.CodeFortressUser;
import dev.codefortress.core.service.JwtService;
import dev.codefortress.core.service.PasswordValidator;
import dev.codefortress.core.service.RefreshTokenService;
import dev.codefortress.core.spi.CodeFortressUserProvider;
import dev.codefortress.web.dto.*;
import dev.codefortress.core.event.CodeFortressUserCreatedEvent; // Importar evento
import org.springframework.context.ApplicationEventPublisher;
import dev.codefortress.web.service.RateLimitService;
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
@RequestMapping("${codefortress.api.auth-path:/auth}")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "codefortress.api", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CodeFortressUserProvider userProvider;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordValidator passwordValidator;
    private final RateLimitService rateLimitService;
    private final CodeFortressProperties properties;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        if (properties.getRateLimit().isEnabled()) {
            String ip = httpRequest.getRemoteAddr();
            Bucket bucket = rateLimitService.resolveBucket(ip);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed()) {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new ErrorResponse(429, "Too Many Requests",
                                "Demasiados intentos. Espera " + waitForRefill + " segundos.",
                                LocalDateTime.now()));
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        CodeFortressUser user = userProvider.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtService.generateToken(user);

        String refreshTokenString = null;

        if (properties.getSecurity().getRefreshToken().isEnabled()) {
            CodeFortressRefreshToken refreshToken = refreshTokenService.createRefreshToken(user.username());
            refreshTokenString = refreshToken.token();
        }

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshTokenString));
    }

    @PostMapping("/register")
    public ResponseEntity<CodeFortressUser> register(@RequestBody RegisterRequest request) {
        passwordValidator.validate(request.password());
        String encodedPassword = passwordEncoder.encode(request.password());

        CodeFortressUser newUser = new CodeFortressUser(
                request.username(),
                encodedPassword,
                request.roles() != null ? request.roles() : new HashSet<>(),
                true
        );

        CodeFortressUser savedUser = userProvider.save(newUser);

        eventPublisher.publishEvent(new CodeFortressUserCreatedEvent(savedUser));
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        if (!properties.getSecurity().getRefreshToken().isEnabled()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        String requestRefreshToken = request.refreshToken();

        return java.util.Optional.of(refreshTokenService.findByToken(requestRefreshToken))
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    CodeFortressUser user = userProvider.findByUsername(token.username()).orElseThrow();
                    String newAccessToken = jwtService.generateToken(user);
                    refreshTokenService.deleteByToken(requestRefreshToken);
                    CodeFortressRefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.username());
                    return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken.token()));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token invalido"));
    }
}