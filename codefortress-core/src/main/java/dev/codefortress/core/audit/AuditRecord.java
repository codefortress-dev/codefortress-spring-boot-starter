package dev.codefortress.core.audit;

import java.time.LocalDateTime;

/**
 * Represents an audit record.
 * @param principal the principal who performed the action
 * @param action the action that was performed
 * @param details additional details about the action
 * @param timestamp the timestamp of the action
 */
public record AuditRecord(
        String principal,
        String action,
        String details,
        LocalDateTime timestamp
) {}