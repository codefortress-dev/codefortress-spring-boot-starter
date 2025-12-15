package dev.example.custom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditEntity, Long> {
    // Método útil para consultar el historial de un usuario específico
    // SQL generado: SELECT * FROM custom_audit_logs WHERE username = ? ORDER BY timestamp DESC
    List<AuditEntity> findByUsernameOrderByTimestampDesc(String username);
}
