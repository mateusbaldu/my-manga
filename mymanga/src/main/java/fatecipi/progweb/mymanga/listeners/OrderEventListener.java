package fatecipi.progweb.mymanga.listeners;

import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final EmailService emailService;

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        Order order = event.order();

        String confirmationUrl = "http://localhost:8080/my-manga/orders/confirm?token=" + order.getConfirmationToken();
        String subject = "Confirm your order #" + order.getId();
        String body = "Hi " + order.getUsers().getName() + ",\n\nThank you for buying with us! Please, confirm your order by clicking on the link down below:\n\n" + confirmationUrl;

        emailService.sendEmail(order.getUsers().getEmail(), subject, body);
    }
}
