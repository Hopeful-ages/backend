package ages.hopeful.modules.cobrades.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import ages.hopeful.modules.services.model.Service;
import ages.hopeful.modules.services.repository.ServiceRepository;
import ages.hopeful.modules.user.dto.UserRequestDTO;
import ages.hopeful.modules.user.dto.UserResponseDTO;
import ages.hopeful.modules.user.dto.UserUpdateDTO;
import ages.hopeful.modules.user.model.Role;
import ages.hopeful.modules.user.model.User;
import ages.hopeful.modules.user.repository.RoleRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases for cobrades")
public class CobradeServiceTest {

    // Test cases for cobrades

    @InjectMocks
    private CobradeService cobradeService;

    @Mock
    private CobradeRepository cobradeRepository;

    private CobradeResponseDTO cobradeResponseDTO;

    private Cobrade cobrade;

    @BeforeEach
    void setUp() {
        cobrade = new Cobrade();
        cobrade.setCode("1.2.3.4");
        cobrade.setDescription("Evento geológico");
        cobrade.setSubgroup("Geológico");
        cobrade.setType("Deslizamento");
        cobrade.setSubType("Terra");

        cobradeResponseDTO = CobradeResponseDTO.builder()
            .code(cobrade.getCode())
            .description(cobrade.getDescription())
            .subgroup(cobrade.getSubgroup())
            .type(cobrade.getType())
            .subType(cobrade.getSubType())
            .build();
    }

    @Test
    void shouldReturnAllCobrades() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.getAllCobrades();

        assertEquals(1, result.size());
        assertEquals("1.2.3.4", result.get(0).getCode());
    }

    @Test
    void shouldReturnCobradesByType() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllByType(
            "Deslizamento"
        );

        assertEquals(1, result.size());
        assertEquals("Deslizamento", result.get(0).getType());
    }

    @Test
    void shouldReturnCobradesBySubtype() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllBySubtype(
            "Terra"
        );

        assertEquals(1, result.size());
        assertEquals("Terra", result.get(0).getSubType());
    }

    @Test
    void shouldReturnCobradesByCode() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllByCode(
            "1.2.3.4"
        );

        assertEquals(1, result.size());
        assertEquals("1.2.3.4", result.get(0).getCode());
    }
}
