package dev.example.custom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomSessionRepository extends JpaRepository<CustomSessionToken, Long> {
    Optional<CustomSessionToken> findByTokenUuid(String token);
    void deleteByTokenUuid(String token);
    // Para limpiar sesiones viejas del usuario
    void deleteByUsuarioEmail(String email);
}