package ages.hopeful.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilities for password hashing
 * Use this class to generate password hashes during development
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        
        String password = "Senha";
        String hash = encoder.encode(password);
        
        System.out.println("Senha original: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println();
        
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificação: " + matches);
    }
}
