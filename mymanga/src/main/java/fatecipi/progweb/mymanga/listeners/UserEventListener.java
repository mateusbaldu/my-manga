package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        Users user = event.user();

        String activationUrl = "http://localhost:8080/my-manga/users/activate?token=" + user.getConfirmationToken();
        String subject = "Activate your account on My Mangá!";
        String body = "Welcome, " + user.getName() + "! Click on the link below to activate your account:\n\n" + activationUrl;

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
