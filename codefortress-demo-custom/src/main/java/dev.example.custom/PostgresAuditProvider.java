package dev.example.custom;

import dev.codefortress.core.audit.AuditRecord;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostgresAuditProvider implements CodeFortressAuditProvider {

    private final AuditLogRepository auditRepo; // Repositorio de tu tabla de logs

    @Override
    public void log(AuditRecord record) {
        // Convertimos el record de la librería a nuestra entidad
        AuditEntity entity = new AuditEntity();
        entity.setUsername(record.principal());
        entity.setAction(record.action());
        entity.setDetail(record.details());
        entity.setTimestamp(record.timestamp());

        auditRepo.save(entity); // ¡Guardado en Postgres!
    }
}