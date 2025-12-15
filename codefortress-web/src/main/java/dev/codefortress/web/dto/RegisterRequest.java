package dev.codefortress.web.dto;

import java.util.Set;

/**
 * Represents a registration request.
 * @param username the username
 * @param password the password
 * @param roles the roles
 */
public record RegisterRequest(String username, String password, Set<String> roles) {}