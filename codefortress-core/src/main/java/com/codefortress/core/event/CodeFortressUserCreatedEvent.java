package com.codefortress.core.event;

import com.codefortress.core.model.CodeFortressUser;

/**
 * Evento que se dispara cuando un usuario se registra exitosamente.
 * Los clientes pueden escuchar esto con @EventListener.
 */
public record CodeFortressUserCreatedEvent(CodeFortressUser user) {}