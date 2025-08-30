package ages.hopeful.modules.users.controllers;

import ages.hopeful.exception.ConflictException;
import ages.hopeful.exception.GlobalExceptionHandler;
import ages.hopeful.modules.users.dto.UserCreateDTO;
import ages.hopeful.modules.users.dto.UserResponseDTO;
import ages.hopeful.modules.users.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // conforme teu guia
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class) // garante o Advice no contexto do @WebMvcTest
class UserControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService service;

  @Test
  void post_quandoPayloadValido_deveRetornar200ComBody() throws Exception {
    var id = UUID.randomUUID();
    var response = UserResponseDTO.builder()
        .id(id)
        .nome("Ana")
        .cpf("12345678909")
        .email("ana@ex.com")
        .telefone("51999999999")
        .servicoId(UUID.randomUUID())
        .cidadeId(UUID.randomUUID())
        .build();

    when(service.save(any(UserCreateDTO.class))).thenReturn(response);

    var req = new UserCreateDTO();
    req.setNome("Ana");
    req.setEmail("ana@ex.com");
    req.setCpf("123.456.789-09");
    req.setTelefone("51999999999");
    req.setSenha("segredo123");
    req.setServicoId(UUID.randomUUID());
    req.setCidadeId(UUID.randomUUID());

    mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.email").value("ana@ex.com"))
        .andExpect(jsonPath("$.cpf").value("12345678909"));

    var captor = ArgumentCaptor.forClass(UserCreateDTO.class);
    verify(service).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("ana@ex.com");
    assertThat(captor.getValue().getCpf()).isEqualTo("123.456.789-09"); // entrada pode vir mascarada
  }

  @Test
  void post_quandoPayloadInvalido_deveRetornar400() throws Exception {
    var req = new UserCreateDTO(); // faltando campos obrigatórios do @Valid

    mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").isArray());
  }

  @Test
  void post_quandoConflito_deveRetornar409() throws Exception {
    when(service.save(any())).thenThrow(new ConflictException("CPF already registered"));

    var req = new UserCreateDTO();
    req.setNome("Ana");
    req.setEmail("ana@ex.com");
    req.setCpf("123.456.789-09");
    req.setTelefone("51999999999");
    req.setSenha("segredo123");

    mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("CPF already registered"));
  }
}
