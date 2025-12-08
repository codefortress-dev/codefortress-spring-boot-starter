package com.codefortress.core.audit;

import java.time.LocalDateTime;

public record AuditRecord(
        String principal,   // Quién (username o IP si es anónimo)
        String action,      // Qué hizo (LOGIN_SUCCESS, REGISTER, LOGIN_FAILURE)
        String details,     // Detalles extra (ej: "IP: 192.168.1.1")
        LocalDateTime timestamp
) {}