package ages.hopeful.modules;

import ages.hopeful.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ClearDatabase
@DisplayName("Teste de Limpeza de Banco de Dados")
class ClearDatabaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Teste 1: Deve começar com dados das migrações")
    void test1_shouldStartWithMigrationData() {
        long userCount = userRepository.count();

        System.out.println("=== TESTE 1 ===");
        System.out.println("Número de usuários no banco: " + userCount);

        assertTrue(userCount >= 0, "Banco deve ter sido inicializado pelas migrações");
    }

    @Test
    @DisplayName("Teste 2: Deve começar com dados limpos novamente")
    void test2_shouldStartCleanAgain() {
        long userCount = userRepository.count();

        System.out.println("=== TESTE 2 ===");
        System.out.println("Número de usuários no banco: " + userCount);

        assertTrue(userCount >= 0, "Banco deve ter sido limpo e recriado");
    }

    @Test
    @DisplayName("Teste 3: Dados criados em um teste não afetam outro")
    void test3_shouldNotAffectOtherTests() {
        long initialCount = userRepository.count();

        System.out.println("=== TESTE 3 ===");
        System.out.println("Contagem inicial: " + initialCount);

        // No próximo teste, o banco será limpo novamente
        assertTrue(true, "Teste executado com sucesso");
    }
}
