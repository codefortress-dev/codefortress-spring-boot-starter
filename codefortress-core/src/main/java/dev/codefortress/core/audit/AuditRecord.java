package dev.codefortress.core.audit;

import java.time.LocalDateTime;

public record AuditRecord(
        String principal,
        String action,
        String details,
        LocalDateTime timestamp
) {}