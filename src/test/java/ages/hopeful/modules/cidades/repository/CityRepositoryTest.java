package ages.hopeful.modules.cidades.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CityRepositoryTest {

    @Autowired
    private CityRepository cidadeRepository;

    @Test
    void testSalvarECidade() {
        UUID id = UUID.randomUUID();
        City cidade = new City(id, "Porto Alegre", "RS");
        cidadeRepository.save(cidade);

        City encontrada = cidadeRepository.findById(id).orElse(null);

        assertThat(encontrada).isNotNull();
        //assertThat(encontrada.getNome()).isEqualTo("Porto Alegre");
        //assertThat(encontrada.getEstado()).isEqualTo("RS");
    }
}
