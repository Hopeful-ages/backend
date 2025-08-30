package ages.hopeful.modules.users.services;

import ages.hopeful.exception.ConflictException;
import ages.hopeful.modules.users.dto.UserCreateDTO;
import ages.hopeful.modules.users.dto.UserResponseDTO;
import ages.hopeful.modules.users.mapper.UserMapper;
import ages.hopeful.modules.users.model.User;
import ages.hopeful.modules.users.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository repo;
    @Mock private BCryptPasswordEncoder encoder;

    private UserService service;

    @BeforeEach
    void setUp() { service = new UserService(repo, encoder); }

    @Test
    void save_quandoEmailJaExiste_deveLancarConflict() {
        var dto = buildDto("ANA@EX.COM", "123.456.789-09", "senha123");
        when(repo.existsByEmail("ana@ex.com")).thenReturn(true);

        var ex = assertThrows(ConflictException.class, () -> service.save(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("email"));

        verify(repo).existsByEmail("ana@ex.com");
        verify(repo, never()).existsByCpf(anyString());
        verify(repo, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void save_quandoCpfJaExiste_deveLancarConflict() {
        var dto = buildDto("ana@ex.com", "123.456.789-09", "senha123");
        when(repo.existsByEmail("ana@ex.com")).thenReturn(false);
        when(repo.existsByCpf("12345678909")).thenReturn(true);

        var ex = assertThrows(ConflictException.class, () -> service.save(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("cpf"));

        verify(repo).existsByEmail("ana@ex.com");
        verify(repo).existsByCpf("12345678909");
        verify(repo, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void save_fluxoFeliz_deveNormalizarCriptografarSalvarERetornarResponse() {
        var dto = buildDto("ANA@EX.COM", "123.456.789-09", "senha123");
        when(repo.existsByEmail("ana@ex.com")).thenReturn(false);
        when(repo.existsByCpf("12345678909")).thenReturn(false);
        when(encoder.encode("senha123")).thenReturn("HASHED");

        UUID id = UUID.randomUUID();

        var toPersist = new User();
        toPersist.setNome("Ana");
        toPersist.setCpf("12345678909");
        toPersist.setEmail("ana@ex.com");
        toPersist.setTelefone("51999999999");
        toPersist.setSenhaHash("HASHED");
        toPersist.setServicoId(UUID.randomUUID());
        toPersist.setCidadeId(UUID.randomUUID());

        var persisted = new User();
        persisted.setId(id);
        persisted.setNome(toPersist.getNome());
        persisted.setCpf(toPersist.getCpf());
        persisted.setEmail(toPersist.getEmail());
        persisted.setTelefone(toPersist.getTelefone());
        persisted.setSenhaHash(toPersist.getSenhaHash());
        persisted.setServicoId(toPersist.getServicoId());
        persisted.setCidadeId(toPersist.getCidadeId());

        var expected = UserResponseDTO.builder()
                .id(id)
                .nome(persisted.getNome())
                .cpf(persisted.getCpf())
                .email(persisted.getEmail())
                .telefone(persisted.getTelefone())
                .servicoId(persisted.getServicoId())
                .cidadeId(persisted.getCidadeId())
                .build();

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto, "HASHED")).thenReturn(toPersist);
            when(repo.save(toPersist)).thenReturn(persisted);
            mocked.when(() -> UserMapper.toResponse(persisted)).thenReturn(expected);

            var result = service.save(dto);

            assertNotNull(result);
            assertEquals(expected.getId(), result.getId());
            assertEquals(expected.getEmail(), result.getEmail());

            verify(repo).existsByEmail("ana@ex.com");
            verify(repo).existsByCpf("12345678909");
            verify(encoder).encode("senha123");
            verify(repo).save(toPersist);
            mocked.verify(() -> UserMapper.toEntity(dto, "HASHED"));
            mocked.verify(() -> UserMapper.toResponse(persisted));
        }
    }

    private static UserCreateDTO buildDto(String email, String cpf, String senha) {
        var dto = new UserCreateDTO();
        dto.setNome("Ana");
        dto.setEmail(email);
        dto.setCpf(cpf);
        dto.setTelefone("51999999999");
        dto.setSenha(senha);
        dto.setServicoId(UUID.randomUUID());
        dto.setCidadeId(UUID.randomUUID());
        return dto;
    }
}
