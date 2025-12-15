package dev.codefortress.web.dto;

/**
 * Represents a refresh token request.
 * @param refreshToken the refresh token
 */
public record RefreshTokenRequest(
        String refreshToken
) {
}
