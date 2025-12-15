package dev.codefortress.core.audit;

/**
 * Service Provider Interface (SPI) for auditing events.
 * Implement this interface to provide a custom auditing mechanism,
 * such as logging to a database, Elasticsearch, Splunk, or files.
 */
public interface CodeFortressAuditProvider {

    /**
     * Logs an audit record.
     *
     * @param record the audit record to log
     */
    void log(AuditRecord record);
}