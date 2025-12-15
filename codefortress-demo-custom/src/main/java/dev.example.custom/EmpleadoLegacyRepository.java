package dev.example.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmpleadoLegacyRepository  extends JpaRepository<EmpleadoLegacy, Long> {
    // Spring Data JPA generará el SQL automáticamente:
    // SELECT * FROM vieja_empleados WHERE email = ?
    Optional<EmpleadoLegacy> findByEmail(String email);
}
