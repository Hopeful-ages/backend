package ages.hopeful.modules.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService();
        // Injetar o mock manualmente já que o @Autowired não funciona em testes unitários puros
        java.lang.reflect.Field field;
        try {
            field = EmailService.class.getDeclaredField("mailSender");
            field.setAccessible(true);
            field.set(emailService, mailSender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendPasswordResetEmail_SendsCorrectEmail() {
        String to = "user@example.com";
        String token = "abc123";

        emailService.sendPasswordResetEmail(to, token);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Redefinição de senha", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("http://localhost:3000/reset-password?token=" + token));
        assertEquals("hopeful.suporte@gmail.com", sentMessage.getFrom());
    }
}
