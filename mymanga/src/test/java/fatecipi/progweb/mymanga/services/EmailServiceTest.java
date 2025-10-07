package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.exceptions.EmailSenderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Nested
    class SendEmail {
        @Test
        @DisplayName("should send email successfully")
        void sendEmail_void_whenEmailIsSend() {
            //Arrange
            String to = "test@gmail.com";
            String subject = "test";
            String body = "test";

            //Act
            emailService.sendEmail(to, subject, body);

            //Assert
            verify(mailSender, times(1)).send(messageCaptor.capture());
        }

        @Test
        @DisplayName("should call system out print when catch a exception")
        void sendEmail_throwEmailSenderException_whenExceptionCatched() {
            //Arrange
            String to = "test@gmail.com";
            String subject = "test";
            String body = "test";
            doThrow(new EmailSenderException("Erro")).when(mailSender).send(messageCaptor.capture());

            //Act & Assert
            assertThrows(EmailSenderException.class, () -> emailService.sendEmail(to, subject, body));
        }
    }
}