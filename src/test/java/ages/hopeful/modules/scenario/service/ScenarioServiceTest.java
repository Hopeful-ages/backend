package ages.hopeful.modules.cobrade.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ages.hopeful.common.exception.ConflictException;
import ages.hopeful.common.exception.NotFoundException;
import ages.hopeful.modules.cobrade.dto.CobradeRequestDTO;
import ages.hopeful.modules.cobrade.dto.CobradeResponseDTO;
import ages.hopeful.modules.cobrade.dto.CobradeUpdateDTO;
import ages.hopeful.modules.cobrade.model.Cobrade;
import ages.hopeful.modules.cobrade.repository.CobradeRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the cobrade service")
public class CobradeServiceTest {

    @InjectMocks
    private CobradeService cobradeService;

    @Mock
    private CobradeRepository cobradeRepository;

    @Mock
    private ModelMapper modelMapper;

    private CobradeRequestDTO cobradeRequestDTO;
    private Cobrade cobrade;
    private CobradeResponseDTO cobradeResponseDTO;
    private CobradeUpdateDTO cobradeUpdateDTO;

    @BeforeEach
    void setUp() {
        cobradeRequestDTO = CobradeRequestDTO.builder()
            .code("1.2.3.4.5")
            .name("Inundação")
            .description("Alagamento por precipitação")
            .category("Hidrológico")
            .build();

        cobradeUpdateDTO = CobradeUpdateDTO.builder()
            .code("1.2.3.4.6")
            .name("Enxurrada")
            .description("Alagamento rápido")
            .category("Hidrológico")
            .build();

        cobrade = new Cobrade();
        cobrade.setId(UUID.randomUUID());
        cobrade.setCode("1.2.3.4.5");
        cobrade.setName("Inundação");
        cobrade.setDescription("Alagamento por precipitação");
        cobrade.setCategory("Hidrológico");

        cobradeResponseDTO = new CobradeResponseDTO();
        cobradeResponseDTO.setId(cobrade.getId());
        cobradeResponseDTO.setCode(cobrade.getCode());
        cobradeResponseDTO.setName(cobrade.getName());
        cobradeResponseDTO.setDescription(cobrade.getDescription());
        cobradeResponseDTO.setCategory(cobrade.getCategory());
    }

    @Test
    @DisplayName("Should create a cobrade successfully when data is valid")
    void shouldCreateCobradeSuccessfullyWhenDataIsValid() {
        when(cobradeRepository.existsByCode(anyString())).thenReturn(false);
        when(modelMapper.map(any(CobradeRequestDTO.class), eq(Cobrade.class)))
            .thenReturn(cobrade);
        when(cobradeRepository.save(any(Cobrade.class))).thenReturn(cobrade);
        when(modelMapper.map(any(Cobrade.class), eq(CobradeResponseDTO.class)))
            .thenReturn(cobradeResponseDTO);

        CobradeResponseDTO response = cobradeService.createCobrade(cobradeRequestDTO);

        assertNotNull(response);
        assertEquals(cobradeResponseDTO.getId(), response.getId());
        assertEquals("1.2.3.4.5", response.getCode());
        assertEquals("Inundação", response.getName());

        verify(cobradeRepository, times(1)).existsByCode(cobradeRequestDTO.getCode());
        verify(cobradeRepository, times(1)).save(cobrade);
    }

    @Test
    @DisplayName("Should throw ConflictException when code already exists")
    void shouldThrowConflictExceptionWhenCodeExists() {
        when(cobradeRepository.existsByCode(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
            cobradeService.createCobrade(cobradeRequestDTO)
        );

        verify(cobradeRepository, times(1)).existsByCode(cobradeRequestDTO.getCode());
        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when code is null")
    void shouldThrowIllegalArgumentExceptionWhenCodeIsNull() {
        cobradeRequestDTO.setCode(null);

        assertThrows(IllegalArgumentException.class, () ->
            cobradeService.createCobrade(cobradeRequestDTO)
        );

        verify(cobradeRepository, never()).existsByCode(anyString());
        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when code is empty")
    void shouldThrowIllegalArgumentExceptionWhenCodeIsEmpty() {
        cobradeRequestDTO.setCode("");

        assertThrows(IllegalArgumentException.class, () ->
            cobradeService.createCobrade(cobradeRequestDTO)
        );

        verify(cobradeRepository, never()).existsByCode(anyString());
        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void shouldThrowIllegalArgumentExceptionWhenNameIsNull() {
        cobradeRequestDTO.setName(null);

        assertThrows(IllegalArgumentException.class, () ->
            cobradeService.createCobrade(cobradeRequestDTO)
        );

        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should update cobrade successfully when data is valid")
    void shouldUpdateCobradeSuccessfullyWhenDataIsValid() {
        UUID cobradeId = cobrade.getId();

        when(cobradeRepository.findById(cobradeId)).thenReturn(Optional.of(cobrade));
        when(cobradeRepository.existsByCode(anyString())).thenReturn(false);
        when(cobradeRepository.save(any(Cobrade.class))).thenReturn(cobrade);

        doAnswer(invocation -> {
            CobradeUpdateDTO dto = invocation.getArgument(0);
            Cobrade entity = invocation.getArgument(1);
            if (dto.getCode() != null) entity.setCode(dto.getCode());
            if (dto.getName() != null) entity.setName(dto.getName());
            if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
            if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
            return null;
        }).when(modelMapper).map(any(CobradeUpdateDTO.class), any(Cobrade.class));

        when(modelMapper.map(any(Cobrade.class), eq(CobradeResponseDTO.class)))
            .thenAnswer(invocation -> {
                Cobrade c = invocation.getArgument(0);
                CobradeResponseDTO dto = new CobradeResponseDTO();
                dto.setId(c.getId());
                dto.setCode(c.getCode());
                dto.setName(c.getName());
                dto.setDescription(c.getDescription());
                dto.setCategory(c.getCategory());
                return dto;
            });

        CobradeResponseDTO response = cobradeService.updateCobrade(cobradeId, cobradeUpdateDTO);

        assertNotNull(response);
        assertEquals(cobrade.getCode(), response.getCode());
        assertEquals(cobrade.getName(), response.getName());
        assertEquals(cobrade.getDescription(), response.getDescription());
        assertEquals(cobrade.getCategory(), response.getCategory());

        verify(cobradeRepository, times(1)).save(cobrade);
    }

    @Test
    @DisplayName("Should throw ConflictException when updating code to one that already exists")
    void shouldThrowConflictExceptionWhenUpdatingCodeToExistingOne() {
        UUID cobradeId = cobrade.getId();

        when(cobradeRepository.findById(cobradeId)).thenReturn(Optional.of(cobrade));
        when(cobradeRepository.existsByCode(cobradeUpdateDTO.getCode())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
            cobradeService.updateCobrade(cobradeId, cobradeUpdateDTO)
        );

        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating a non-existent cobrade")
    void shouldThrowNotFoundExceptionWhenCobradeDoesNotExist() {
        UUID invalidId = UUID.randomUUID();
        when(cobradeRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            cobradeService.updateCobrade(invalidId, cobradeUpdateDTO)
        );

        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should find cobrade by id successfully")
    void shouldFindCobradeByIdSuccessfully() {
        UUID cobradeId = cobrade.getId();

        when(cobradeRepository.findById(cobradeId)).thenReturn(Optional.of(cobrade));
        when(modelMapper.map(any(Cobrade.class), eq(CobradeResponseDTO.class)))
            .thenReturn(cobradeResponseDTO);

        CobradeResponseDTO response = cobradeService.getCobradeById(cobradeId);

        assertNotNull(response);
        assertEquals(cobradeResponseDTO.getId(), response.getId());
        assertEquals(cobradeResponseDTO.getCode(), response.getCode());

        verify(cobradeRepository, times(1)).findById(cobradeId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when cobrade not found by id")
    void shouldThrowNotFoundExceptionWhenCobradeNotFoundById() {
        UUID invalidId = UUID.randomUUID();
        when(cobradeRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            cobradeService.getCobradeById(invalidId)
        );

        verify(cobradeRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should delete cobrade successfully")
    void shouldDeleteCobradeSuccessfully() {
        UUID cobradeId = cobrade.getId();

        when(cobradeRepository.findById(cobradeId)).thenReturn(Optional.of(cobrade));
        doNothing().when(cobradeRepository).delete(cobrade);

        cobradeService.deleteCobrade(cobradeId);

        verify(cobradeRepository, times(1)).findById(cobradeId);
        verify(cobradeRepository, times(1)).delete(cobrade);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting a non-existent cobrade")
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentCobrade() {
        UUID invalidId = UUID.randomUUID();
        when(cobradeRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            cobradeService.deleteCobrade(invalidId)
        );

        verify(cobradeRepository, times(1)).findById(invalidId);
        verify(cobradeRepository, never()).delete(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should create cobrade when code follows valid format")
    void shouldCreateCobradeWhenCodeFollowsValidFormat() {
        cobradeRequestDTO.setCode("1.2.3.4.5");

        when(cobradeRepository.existsByCode(anyString())).thenReturn(false);
        when(modelMapper.map(any(CobradeRequestDTO.class), eq(Cobrade.class)))
            .thenReturn(cobrade);
        when(cobradeRepository.save(any(Cobrade.class))).thenReturn(cobrade);
        when(modelMapper.map(any(Cobrade.class), eq(CobradeResponseDTO.class)))
            .thenReturn(cobradeResponseDTO);

        CobradeResponseDTO response = cobradeService.createCobrade(cobradeRequestDTO);

        assertNotNull(response);
        assertEquals("1.2.3.4.5", response.getCode());
        
        verify(cobradeRepository, times(1)).existsByCode(cobradeRequestDTO.getCode());
        verify(cobradeRepository, times(1)).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when code format is invalid")
    void shouldThrowWhenCodeFormatIsInvalid() {
        cobradeRequestDTO.setCode("123456");

        assertThrows(IllegalArgumentException.class, () -> 
            cobradeService.createCobrade(cobradeRequestDTO)
        );
        
        verify(cobradeRepository, never()).save(any(Cobrade.class));
    }

    @Test
    @DisplayName("Should allow updating cobrade with same code")
    void shouldAllowUpdatingCobradeWithSameCode() {
        UUID cobradeId = cobrade.getId();
        cobradeUpdateDTO.setCode("1.2.3.4.5"); // mesmo código do cobrade existente

        when(cobradeRepository.findById(cobradeId)).thenReturn(Optional.of(cobrade));
        when(cobradeRepository.save(any(Cobrade.class))).thenReturn(cobrade);

        doAnswer(invocation -> {
            CobradeUpdateDTO dto = invocation.getArgument(0);
            Cobrade entity = invocation.getArgument(1);
            if (dto.getName() != null) entity.setName(dto.getName());
            return null;
        }).when(modelMapper).map(any(CobradeUpdateDTO.class), any(Cobrade.class));

        when(modelMapper.map(any(Cobrade.class), eq(CobradeResponseDTO.class)))
            .thenReturn(cobradeResponseDTO);

        CobradeResponseDTO response = cobradeService.updateCobrade(cobradeId, cobradeUpdateDTO);

        assertNotNull(response);
        verify(cobradeRepository, times(1)).save(cobrade);
    }
}