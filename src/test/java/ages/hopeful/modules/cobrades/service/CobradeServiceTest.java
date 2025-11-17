package ages.hopeful.modules.cobrades.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrades.model.Cobrade;
import ages.hopeful.modules.cobrades.repository.CobradeRepository;
import jakarta.persistence.EntityNotFoundException; // Importe a exceção correta
import java.util.Collections;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CobradeService")
public class CobradeServiceTest {

    @InjectMocks
    private CobradeService cobradeService;

    @Mock
    private CobradeRepository cobradeRepository;

    private Cobrade cobrade;
    private Cobrade cobrade2;
    private UUID testId1;
    private UUID testId2;

    @BeforeEach
    void setUp() {
        testId1 = UUID.randomUUID();
        testId2 = UUID.randomUUID();

        cobrade = new Cobrade();
        cobrade.setId(testId1);
        cobrade.setCode("1.2.3.4");
        cobrade.setDescription("Evento geológico");
        cobrade.setSubgroup("Geológico");
        cobrade.setType("Deslizamento");
        cobrade.setSubType("Terra");
        cobrade.setGroup("Grupo 1");
        cobrade.setOrigin("Natural");

        cobrade2 = new Cobrade();
        cobrade2.setId(testId2);
        cobrade2.setCode("5.6.7.8");
        cobrade2.setDescription("Evento hidrológico");
        cobrade2.setSubgroup("Hidrológico");
        cobrade2.setType("Inundação");
        cobrade2.setSubType("Água");
        cobrade2.setGroup("Grupo 2");
        cobrade2.setOrigin("Natural");
    }

    @Test
    @DisplayName("Deve retornar todos os Cobrades")
    void shouldReturnAllCobrades() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade, cobrade2));

        List<CobradeResponseDTO> result = cobradeService.getAllCobrades();

        assertEquals(2, result.size());
        assertEquals("1.2.3.4", result.get(0).getCode());
        assertEquals("5.6.7.8", result.get(1).getCode());
        verify(cobradeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver Cobrades")
    void shouldReturnEmptyListWhenNoCobrades() {
        when(cobradeRepository.findAll()).thenReturn(Collections.emptyList());

        List<CobradeResponseDTO> result = cobradeService.getAllCobrades();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cobradeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar Cobrade DTO por ID quando encontrado")
    void shouldReturnCobradeById() {
        // Arrange
        when(cobradeRepository.findById(testId1)).thenReturn(Optional.of(cobrade));

        // Act
        CobradeResponseDTO result = cobradeService.getCobradeById(testId1);

        // Assert
        assertNotNull(result);
        assertEquals(testId1, result.getId());
        assertEquals("1.2.3.4", result.getCode());
        verify(cobradeRepository, times(1)).findById(testId1);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar DTO por ID não existente")
    void shouldThrowExceptionWhenCobradeByIdNotFound() {
        UUID notFoundId = UUID.randomUUID();
        when(cobradeRepository.findById(notFoundId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cobradeService.getCobradeById(notFoundId);
        });

        assertEquals("Cobrade not found with id: " + notFoundId, exception.getMessage());
        verify(cobradeRepository, times(1)).findById(notFoundId);
    }

    @Test
    @DisplayName("Deve retornar entidade Cobrade por ID quando encontrada")
    void shouldReturnCobradeEntityById() {
        when(cobradeRepository.findById(testId1)).thenReturn(Optional.of(cobrade));

        Cobrade result = cobradeService.getCobradeEntityById(testId1);

        assertNotNull(result);
        assertEquals(testId1, result.getId());
        assertEquals("Evento geológico", result.getDescription());
        verify(cobradeRepository, times(1)).findById(testId1);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar entidade por ID não existente")
    void shouldThrowExceptionWhenCobradeEntityByIdNotFound() {
        UUID notFoundId = UUID.randomUUID();
        when(cobradeRepository.findById(notFoundId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cobradeService.getCobradeEntityById(notFoundId);
        });

        assertEquals("Cobrade not found with id: " + notFoundId, exception.getMessage());
        verify(cobradeRepository, times(1)).findById(notFoundId);
    }


    @Test
    @DisplayName("findAllFilter deve chamar getAllCobrades quando todos os filtros forem nulos")
    void shouldCallGetAllCobradesWhenAllFiltersAreNull() {
        when(cobradeRepository.findAll()).thenReturn(List.of(cobrade)); 

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(null, null, null, null);

        verify(cobradeRepository, times(1)).findAll(); 
        verify(cobradeRepository, never()).findAll(any(Specification.class)); 
        assertEquals(1, result.size());
        assertEquals("1.2.3.4", result.get(0).getCode());
    }

    @Test
    @DisplayName("findAllFilter deve filtrar por 'code' quando não nulo")
    void shouldCallFilterMethodWhenCodeIsNotNull() {
        String code = "1.2.3.4";
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(null, null, null, code);

        verify(cobradeRepository, never()).findAll();
        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(code, result.get(0).getCode());
    }

    @Test
    @DisplayName("findAllFilter deve filtrar por 'type' quando não nulo")
    void shouldCallFilterMethodWhenTypeIsNotNull() {
        String type = "Deslizamento";
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(type, null, null, null);

        verify(cobradeRepository, never()).findAll();
        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findAllFilter deve filtrar por 'subtype' quando não nulo")
    void shouldCallFilterMethodWhenSubTypeIsNotNull() {
        String subtype = "Terra";
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(null, null, subtype, null);

        verify(cobradeRepository, never()).findAll();
        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findAllFilter deve filtrar por 'subgroup' quando não nulo")
    void shouldCallFilterMethodWhenSubgroupIsNotNull() {
        String subgroup = "Geológico";
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(null, subgroup, null, null);

        verify(cobradeRepository, never()).findAll();
        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findAllFilter deve filtrar por todos os parâmetros combinados")
    void shouldCallFilterMethodWithAllParameters() {
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(List.of(cobrade));

        List<CobradeResponseDTO> result = cobradeService.findAllFilter(
            "Deslizamento", "Geológico", "Terra", "1.2.3.4"
        );

        verify(cobradeRepository, never()).findAll();
        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1.2.3.4", result.get(0).getCode());
    }

    @Test
    @DisplayName("findAllFilter deve retornar lista vazia se o filtro não encontrar nada")
    void shouldReturnEmptyListWhenFilterFindsNothing() {
        when(cobradeRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<CobradeResponseDTO> result = cobradeService.findAllFilter("Tipo Inexistente", null, null, null);

        verify(cobradeRepository, times(1)).findAll(any(Specification.class));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}