package dev.codefortress.starter.audit;

import dev.codefortress.core.audit.AuditRecord;
import dev.codefortress.core.audit.CodeFortressAuditProvider;
import dev.codefortress.core.event.CodeFortressUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CodeFortressAuditListener {

    private final CodeFortressAuditProvider auditProvider;

    @EventListener
    public void onUserRegistered(CodeFortressUserCreatedEvent event) {
        auditProvider.log(new AuditRecord(
                event.user().username(),
                "REGISTER_USER",
                "Roles: " + event.user().roles(),
                LocalDateTime.now()
        ));
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        // Extraer usuario del principal
        String username = "unknown";
        if (event.getAuthentication().getPrincipal() instanceof UserDetails ud) {
            username = ud.getUsername();
        } else {
            username = event.getAuthentication().getName();
        }

        auditProvider.log(new AuditRecord(
                username,
                "LOGIN_SUCCESS",
                "Authority: " + event.getAuthentication().getAuthorities(),
                LocalDateTime.now()
        ));
    }

    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        auditProvider.log(new AuditRecord(
                username,
                "LOGIN_FAILURE",
                "Error: " + event.getException().getMessage(),
                LocalDateTime.now()
        ));
    }
}