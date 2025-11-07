package ages.hopeful.modules.cobrades.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases for cobrades")
public class CobradeServiceTest {


    @InjectMocks
    private CobradeService cobradeService;

    @Mock
    private CobradeRepository cobradeRepository;

    private Cobrade cobrade;

    @BeforeEach
    void setUp() {
        cobrade = new Cobrade();
        cobrade.setCode("1.2.3.4");
        cobrade.setDescription("Evento geológico");
        cobrade.setSubgroup("Geológico");
        cobrade.setType("Deslizamento");
        cobrade.setSubType("Terra");

        CobradeResponseDTO.builder()
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
    void shouldReturnEmptyListWhenNoCobrades() {
        when(cobradeRepository.findAll()).thenReturn(List.of());

        List<CobradeResponseDTO> result = cobradeService.getAllCobrades();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    

}
