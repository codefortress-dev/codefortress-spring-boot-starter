package dev.codefortress.web.dto;

/**
 * Represents a token response.
 * @param accessToken the access token
 * @param refreshToken the refresh token
 */
public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
