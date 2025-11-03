package ages.hopeful.common.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendPasswordResetEmail(String to, String token) {
        // TODO: Implementar envio de e-mail com a API do Gmail
        System.out.println("Enviando e-mail de redefinição de senha para: " + to);
        System.out.println("Token: " + token);
    }
}
