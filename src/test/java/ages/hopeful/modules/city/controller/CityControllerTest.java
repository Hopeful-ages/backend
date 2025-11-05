package ages.hopeful.modules.city.controller;

import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.city.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CityController")
public class CityControllerTest {

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    @DisplayName("Should return list of cities and status 200")
    void shouldReturnListOfCities() throws Exception {
        CityResponseDTO city = new CityResponseDTO(UUID.randomUUID(), "Porto Alegre", "RS");
        List<CityResponseDTO> cities = Collections.singletonList(city);

        when(cityService.getAllCities()).thenReturn(cities);

        mockMvc.perform(get("/api/city")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Porto Alegre")));
    }
    
    @Test
    @DisplayName("Should return empty list when service returns no cities")
    void shouldReturnEmptyListWhenServiceReturnsNoCities() throws Exception {
        when(cityService.getAllCities()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/city")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}