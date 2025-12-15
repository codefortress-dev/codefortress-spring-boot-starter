package dev.codefortress.core.model;

import java.util.Set;

/**
 * Represents the agnostic data model for a user within the CodeFortress ecosystem.
 * <p>
 * This record is a plain data carrier and is not tied to any specific persistence framework.
 * It is designed to be a neutral representation of a user's essential information.
 *
 * @param username the user's unique username
 * @param password the user's encrypted password
 * @param roles    a set of roles assigned to the user
 * @param enabled  whether the user account is enabled
 */
public record CodeFortressUser(
        String username,
        String password,
        Set<String> roles,
        boolean enabled
) {}
