package dev.codefortress.core.spi;

import dev.codefortress.core.model.CodeFortressUser;
import java.util.Optional;

/**
 * Service Provider Interface (SPI) for providing user data.
 * This interface should be implemented to integrate with a custom user repository.
 */
public interface CodeFortressUserProvider {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an optional containing the user if found, or an empty optional otherwise
     */
    Optional<CodeFortressUser> findByUsername(String username);

    /**
     * Saves a user.
     *
     * @param user the user to save
     * @return the saved user
     */
    CodeFortressUser save(CodeFortressUser user);
}