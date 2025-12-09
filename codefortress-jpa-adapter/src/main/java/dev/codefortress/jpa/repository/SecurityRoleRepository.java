package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.SecurityRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SecurityRoleRepository extends JpaRepository<SecurityRoleEntity, Long> {
    Optional<SecurityRoleEntity> findByName(String name);
}