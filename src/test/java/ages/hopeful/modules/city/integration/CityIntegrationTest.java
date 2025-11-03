package ages.hopeful.modules.city.integration;

import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import ages.hopeful.modules.user.repository.UserRepository;
import ages.hopeful.modules.scenarios.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("City Integration Tests")
public class CityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @BeforeEach
    void setUp() {
        scenarioRepository.findAll().forEach(scenario -> scenarioRepository.delete(scenario));
        userRepository.deleteAll();
        cityRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when no cities")
    void shouldReturnEmptyListWhenNoCities() throws Exception {
        mockMvc.perform(get("/api/city")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return list with registered cities")
    void shouldReturnListWithRegisteredCities() throws Exception {
        City city = new City();
        city.setName("Porto Alegre");
        city.setState("RS");
        cityRepository.save(city);

        mockMvc.perform(get("/api/city")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Porto Alegre")));
    }
}