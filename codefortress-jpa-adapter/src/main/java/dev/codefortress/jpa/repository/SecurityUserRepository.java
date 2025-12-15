package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link SecurityUserEntity}.
 */
@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUserEntity, Long> {

    /**
     * Finds a security user by their username.
     *
     * @param username the username to search for
     * @return an optional containing the security user if found, or an empty optional otherwise
     */
    Optional<SecurityUserEntity> findByUsername(String username);
}