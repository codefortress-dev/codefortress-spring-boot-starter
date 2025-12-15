package dev.codefortress.core.spi;

import dev.codefortress.core.model.CodeFortressRefreshToken;
import java.util.Optional;

/**
 * Service Provider Interface (SPI) for providing refresh token data.
 * This interface should be implemented to integrate with a custom refresh token repository.
 */
public interface CodeFortressRefreshTokenProvider {

    /**
     * Creates a new refresh token for the given user.
     *
     * @param username     the username of the user
     * @param expirationMs the expiration time in milliseconds
     * @return the created refresh token
     */
    CodeFortressRefreshToken create(String username, long expirationMs);

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return an optional containing the refresh token if found, or an empty optional otherwise
     */
    Optional<CodeFortressRefreshToken> findByToken(String token);

    /**
     * Deletes a refresh token by its token string.
     *
     * @param token the token string of the refresh token to delete
     */
    void deleteByToken(String token);

    /**
     * Deletes all refresh tokens for a given user.
     *
     * @param username the username of the user
     */
    void deleteByUsername(String username);
}