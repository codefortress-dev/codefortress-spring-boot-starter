package dev.codefortress.web.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
