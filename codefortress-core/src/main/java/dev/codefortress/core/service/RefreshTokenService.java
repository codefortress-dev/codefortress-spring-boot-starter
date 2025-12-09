package dev.codefortress.core.service;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.exception.CodeFortressException;
import dev.codefortress.core.model.CodeFortressRefreshToken;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final CodeFortressProperties properties;
    private final CodeFortressRefreshTokenProvider provider;

    public CodeFortressRefreshToken createRefreshToken(String username) {
        return provider.create(username, properties.getSecurity().getRefreshToken().getExpirationMs());
    }

    public CodeFortressRefreshToken verifyExpiration(CodeFortressRefreshToken token) {
        if (token.expiryDate().isBefore(Instant.now())) {
            provider.deleteByToken(token.token());
            throw new CodeFortressException("Refresh token expirado. Por favor inicie sesión nuevamente.");
        }
        return token;
    }

    public CodeFortressRefreshToken findByToken(String token) {
        return provider.findByToken(token)
                .orElseThrow(() -> new CodeFortressException("Refresh token no encontrado en la base de datos."));
    }

    // Rotación de token: Borra el viejo
    public void deleteByToken(String token) {
        provider.deleteByToken(token);
    }
}