package dev.codefortress.core.service;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.exception.CodeFortressException;
import dev.codefortress.core.model.CodeFortressRefreshToken;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service for handling refresh tokens.
 * This includes creating, verifying, and deleting refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final CodeFortressProperties properties;
    private final CodeFortressRefreshTokenProvider provider;

    /**
     * Creates a new refresh token for the given user.
     *
     * @param username the username of the user
     * @return the created refresh token
     */
    public CodeFortressRefreshToken createRefreshToken(String username) {
        return provider.create(username, properties.getSecurity().getRefreshToken().getExpirationMs());
    }

    /**
     * Verifies that a refresh token has not expired.
     *
     * @param token the refresh token to verify
     * @return the refresh token if it is valid
     * @throws CodeFortressException if the refresh token has expired
     */
    public CodeFortressRefreshToken verifyExpiration(CodeFortressRefreshToken token) {
        if (token.expiryDate().isBefore(Instant.now())) {
            provider.deleteByToken(token.token());
            throw new CodeFortressException("Refresh token expirado. Por favor inicie sesión nuevamente.");
        }
        return token;
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return the refresh token if found
     * @throws CodeFortressException if the refresh token is not found
     */
    public CodeFortressRefreshToken findByToken(String token) {
        return provider.findByToken(token)
                .orElseThrow(() -> new CodeFortressException("Refresh token no encontrado en la base de datos."));
    }

    /**
     * Deletes a refresh token by its token string.
     * This is used for token rotation.
     *
     * @param token the token string of the refresh token to delete
     */
    public void deleteByToken(String token) {
        provider.deleteByToken(token);
    }
}