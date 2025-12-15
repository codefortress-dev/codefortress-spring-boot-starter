package dev.codefortress.core.spi;

import dev.codefortress.core.model.CodeFortressUser;
import java.util.Optional;

/**
 * Service Provider Interface (SPI).
 * El módulo JPA implementará esto.
 * O el usuario creará su propio Bean implementando esto para sistemas Legacy.
 */
public interface CodeFortressUserProvider {
    Optional<CodeFortressUser> findByUsername(String username);
    CodeFortressUser save(CodeFortressUser user);
}