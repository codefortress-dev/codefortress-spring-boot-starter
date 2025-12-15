package dev.codefortress.starter.audit;

import dev.codefortress.core.audit.AuditRecord;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default implementation of {@link CodeFortressAuditProvider} that logs audit records using SLF4J.
 * This provider is used when no other {@code CodeFortressAuditProvider} bean is defined.
 */
public class LoggerAuditProvider implements CodeFortressAuditProvider {

    private static final Logger log = LoggerFactory.getLogger(LoggerAuditProvider.class);

    /**
     * Logs an audit record at the INFO level.
     *
     * @param record the audit record to log
     */
    @Override
    public void log(AuditRecord record) {
        log.info("[AUDIT] [{}] User: {} | Time: {} | Details: {}",
                record.action(),
                record.principal(),
                record.timestamp(),
                record.details()
        );
    }
}
