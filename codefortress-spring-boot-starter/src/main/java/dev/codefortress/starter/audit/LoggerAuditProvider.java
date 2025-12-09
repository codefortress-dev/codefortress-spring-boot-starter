package dev.codefortress.starter.audit;

import dev.codefortress.core.audit.AuditRecord;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerAuditProvider implements CodeFortressAuditProvider {

    private static final Logger log = LoggerFactory.getLogger(LoggerAuditProvider.class);

    @Override
    public void log(AuditRecord record) {
        // Formato: [AUDIT] [ACTION] User: ... Details: ...
        log.info("[AUDIT] [{}] User: {} | Time: {} | Details: {}",
                record.action(),
                record.principal(),
                record.timestamp(),
                record.details()
        );
    }
}
