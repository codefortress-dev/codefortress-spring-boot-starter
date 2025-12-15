package dev.codefortress.core.model;

import java.time.Instant;

/**
 * Represents the agnostic data model for a refresh token.
 * <p>
 * This record is a plain data carrier and is not tied to any specific persistence framework.
 * It is designed to be a neutral representation of a refresh token's essential information.
 *
 * @param token      the refresh token string
 * @param username   the username of the user to whom the token belongs
 * @param expiryDate the expiration date of the token
 */
public record CodeFortressRefreshToken(
        String token,
        String username,
        Instant expiryDate
) {}