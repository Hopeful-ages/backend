package ages.hopeful.modules.users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ---------- CASOS VÁLIDOS ----------

    @Test
    void quandoTodosCamposValidos_cpfComMascara_deveSerValido() {
        var dto = new UserCreateDTO();
        dto.setNome("Ana");
        dto.setCpf("123.456.789-09"); // 11-14 chars, só dígitos . -
        dto.setEmail("ana@ex.com");
        dto.setTelefone("51999999999"); // opcional
        dto.setSenha("segredo123");     // >= 6
        dto.setServicoId(UUID.randomUUID());
        dto.setCidadeId(UUID.randomUUID());

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).isEmpty();
    }

    @Test
    void quandoTodosCamposValidos_cpfSemMascara_deveSerValido() {
        var dto = new UserCreateDTO();
        dto.setNome("João");
        dto.setCpf("12345678909");      // 11 dígitos, sem máscara
        dto.setEmail("joao@ex.com");
        dto.setTelefone(null);          // opcional
        dto.setSenha("senhaOk6");

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).isEmpty();
    }

    // ---------- CASOS INVÁLIDOS (OBRIGATÓRIOS/FORMATO) ----------

    @Test
    void quandoObrigatoriosVazios_deveApontarViolacoes() {
        var dto = new UserCreateDTO(); // todos null/blank

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);

        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("nome", "cpf", "email", "senha");
    }

    @Test
    void quandoEmailInvalido_deveApontarEmail() {
        var dto = baseValido();
        dto.setEmail("email-invalido"); // sem @

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("email");
    }

    @Test
    void quandoCpfForaDoPadrao_deveApontarCpf() {
        var dto = baseValido();
        dto.setCpf("1234567890A"); // contém letra -> viola regex ^[0-9.\-]{11,14}$

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("cpf");
    }

    @Test
    void quandoSenhaCurta_deveApontarSenha() {
        var dto = baseValido();
        dto.setSenha("12345"); // < 6

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("senha");
    }

    // ---------- LIMITES DE TAMANHO ----------

    @Test
    void quandoNomeUltrapassa150_deveApontarNome() {
        var dto = baseValido();
        dto.setNome("A".repeat(151));

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("nome");
    }

    @Test
    void quandoEmailUltrapassa160_deveApontarEmail() {
        var dto = baseValido();
        String local = "a".repeat(150);
        dto.setEmail(local + "@ex.com"); // > 160

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("email");
    }

    @Test
    void quandoTelefoneUltrapassa30_deveApontarTelefone() {
        var dto = baseValido();
        dto.setTelefone("9".repeat(31));

        Set<ConstraintViolation<UserCreateDTO>> v = validator.validate(dto);
        assertThat(v).extracting(cv -> cv.getPropertyPath().toString())
            .contains("telefone");
    }

    // ---------- HELPERS ----------

    private static UserCreateDTO baseValido() {
        var dto = new UserCreateDTO();
        dto.setNome("Nome Válido");
        dto.setCpf("123.456.789-09");
        dto.setEmail("valido@ex.com");
        dto.setTelefone("51999999999");
        dto.setSenha("segredo123");
        dto.setServicoId(UUID.randomUUID());
        dto.setCidadeId(UUID.randomUUID());
        return dto;
    }
}
