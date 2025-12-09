package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUserEntity, Long> {
    Optional<SecurityUserEntity> findByUsername(String username);
}