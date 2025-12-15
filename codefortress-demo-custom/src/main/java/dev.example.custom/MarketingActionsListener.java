package dev.example.custom;

import dev.codefortress.core.event.CodeFortressUserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MarketingActionsListener {

    @EventListener
    public void alRegistrarse(CodeFortressUserCreatedEvent event) {
        // Esto simula una lógica de negocio compleja que la librería desconoce
        System.out.println("📧 [MOCK EMAIL] Enviando cupón de descuento a: " + event.user().username());

        // Aquí podrías inyectar 'JavaMailSender' y mandar un correo real.
    }
}