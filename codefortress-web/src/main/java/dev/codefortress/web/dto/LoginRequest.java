package dev.codefortress.web.dto;

/**
 * Represents a login request.
 * @param username the username
 * @param password the password
 */
public record LoginRequest(String username, String password) {}