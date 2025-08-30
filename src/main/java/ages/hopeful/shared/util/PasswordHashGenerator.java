package ages.hopeful.shared.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hashes de senhas BCrypt
 * Use esta classe para gerar hashes de senhas durante desenvolvimento
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Exemplo: gera hash para a senha "Senha"
        String password = "Senha";
        String hash = encoder.encode(password);
        
        System.out.println("Senha original: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println();
        
        // Verifica se o hash está correto
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificação: " + matches);
    }
}
