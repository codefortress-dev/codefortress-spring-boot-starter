package dev.codefortress.core.model;

import java.util.Set;

/**
 * Representación agnóstica de un usuario.
 * No tiene anotaciones @Entity ni @Id. Es pura data.
 */
public record CodeFortressUser(
        String username,
        String password, // Debe estar hasheada
        Set<String> roles,
        boolean enabled
) {}
