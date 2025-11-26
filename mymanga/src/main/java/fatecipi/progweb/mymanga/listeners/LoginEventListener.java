package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginEventListener {
    private final EmailService emailService;

    @EventListener
    public void onResetPasswordRequested(PasswordResetRequestedEvent event) {
        Users user = event.user();

        String resetUrl = "http://localhost:4200/reset-password?token=" + user.getConfirmationToken();
        String subject = "Instruções para Redefinição de Senha - My Mangá";
        String body = String.format("""
        Olá %s,
        
        Você solicitou a redefinição da sua senha.
        
        Por favor, clique no link abaixo para criar uma nova senha:
        
        %s
        
        (Se você não solicitou isso, pode ignorar este e-mail.)
        """, user.getName(), resetUrl);

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
