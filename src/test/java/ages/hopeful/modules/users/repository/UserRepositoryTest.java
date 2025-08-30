package ages.hopeful.modules.users.repository;

import ages.hopeful.modules.users.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository repo;

  // ✅ valida existsByEmail / existsByCpf
  @Test
  void existsByEmail_e_existsByCpf_devemResponderCorretamente() {
    var u = novoUsuario(
        "Ana",
        "ana@ex.com",
        "12345678909",
        "51999999999",
        "HASH"
    );
    repo.saveAndFlush(u);

    assertThat(repo.existsByEmail("ana@ex.com")).isTrue();
    assertThat(repo.existsByCpf("12345678909")).isTrue();

    assertThat(repo.existsByEmail("outra@ex.com")).isFalse();
    assertThat(repo.existsByCpf("00000000000")).isFalse();
  }

  // ✅ valida constraint única de EMAIL
  @Test
  void salvar_quandoEmailDuplicado_deveFalharComConstraint() {
    var a = novoUsuario("A", "dup@ex.com", "11111111111", "51911111111", "HASH");
    repo.saveAndFlush(a);

    var b = novoUsuario("B", "dup@ex.com", "22222222222", "51922222222", "HASH");

    assertThatThrownBy(() -> repo.saveAndFlush(b))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  // ✅ valida constraint única de CPF
  @Test
  void salvar_quandoCpfDuplicado_deveFalharComConstraint() {
    var a = novoUsuario("A", "a@ex.com", "33333333333", "51911111111", "HASH");
    repo.saveAndFlush(a);

    var b = novoUsuario("B", "b@ex.com", "33333333333", "51922222222", "HASH");

    assertThatThrownBy(() -> repo.saveAndFlush(b))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /** Helper para criar a entidade preenchendo campos obrigatórios. */
  private static User novoUsuario(String nome, String email, String cpf, String telefone, String senhaHash) {
    var u = new User();
    u.setId(null); // deixa o JPA gerar se for @GeneratedValue; se não, ignora
    u.setNome(nome);
    u.setEmail(email);
    u.setCpf(cpf);               // ⚠️ sem máscara (o service normaliza)
    u.setTelefone(telefone);
    u.setSenhaHash(senhaHash);
    // Se forem obrigatórios no teu modelo, define também:
    u.setServicoId(UUID.randomUUID());
    u.setCidadeId(UUID.randomUUID());
    return u;
  }
}
