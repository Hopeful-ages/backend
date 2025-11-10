package ages.hopeful.modules.city.service;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CityService")
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    private City city;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(UUID.randomUUID());
        city.setName("Porto Alegre");
        city.setState("RS");
    }

    @Test
    @DisplayName("1. Deve retornar lista de cidades corretamente - getAllCities()")
    void shouldReturnListOfCitiesSuccessfully() {
        when(cityRepository.findAll()).thenReturn(Collections.singletonList(city));

        List<CityResponseDTO> result = cityService.getAllCities();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Porto Alegre");
        assertThat(result.get(0).getState()).isEqualTo("RS");
    }

    @Test
    @DisplayName("2. Deve retornar cidade quando o ID existir - getCityById()")
    void shouldReturnCityByIdSuccessfully() {
        when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));

        City result = cityService.getCityById(city.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Porto Alegre");
        assertThat(result.getState()).isEqualTo("RS");
    }

    @Test
    @DisplayName("3. Deve lançar exceção quando cidade não for encontrada - getCityById()")
    void shouldThrowExceptionWhenCityNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(cityRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.getCityById(fakeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cidade não encontrada");
    }
}
