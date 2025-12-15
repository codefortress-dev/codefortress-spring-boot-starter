package dev.example.demo;

import dev.codefortress.core.event.CodeFortressUserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceSimulator {

    @EventListener
    public void handleUserRegistration(CodeFortressUserCreatedEvent event) {
        System.out.println("📨 [EVENTO RECIBIDO] Enviando Email de Bienvenida a: " + event.user().username());
        System.out.println("   -> Roles asignados: " + event.user().roles());
    }
}