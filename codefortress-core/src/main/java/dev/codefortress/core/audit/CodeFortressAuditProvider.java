package dev.codefortress.core.audit;

/**
 * El usuario puede implementar esto para guardar logs en Base de Datos,
 * ElasticSearch, Splunk, archivos, etc.
 */
public interface CodeFortressAuditProvider {
    void log(AuditRecord record);
}