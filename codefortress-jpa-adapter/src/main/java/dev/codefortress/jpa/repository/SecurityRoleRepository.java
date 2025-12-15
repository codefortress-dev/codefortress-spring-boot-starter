package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.SecurityRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * JPA repository for {@link SecurityRoleEntity}.
 */
public interface SecurityRoleRepository extends JpaRepository<SecurityRoleEntity, Long> {

    /**
     * Finds a security role by its name.
     *
     * @param name the name to search for
     * @return an optional containing the security role if found, or an empty optional otherwise
     */
    Optional<SecurityRoleEntity> findByName(String name);
}