package dev.codefortress.web.dto;

import java.time.LocalDateTime;

/**
 * Represents an error response.
 * @param status the HTTP status code
 * @param error the error message
 * @param message the detailed message
 * @param timestamp the timestamp of the error
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {}
