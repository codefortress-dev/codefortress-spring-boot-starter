package dev.codefortress.core.event;

import dev.codefortress.core.model.CodeFortressUser;

/**
 * Event that is fired when a user is successfully registered.
 * Clients can listen for this event using {@code @EventListener}.
 *
 * @param user the user that was created
 */
public record CodeFortressUserCreatedEvent(CodeFortressUser user) {}