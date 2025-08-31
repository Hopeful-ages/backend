package ages.hopeful.modules.cidades.repository;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ages.hopeful.modules.cidades.model.Cidade;
import ages.hopeful.modules.cidades.repository.CidadeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional 
public class CidadeRepositoryTest {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    void testSalvarECidade() {
        UUID id = UUID.randomUUID();
        Cidade cidade = new Cidade(id, "Porto Alegre", "RS");
        cidadeRepository.save(cidade);

        Cidade encontrada = cidadeRepository.findById(id).orElse(null);

        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getNome()).isEqualTo("Porto Alegre");
        assertThat(encontrada.getEstado()).isEqualTo("RS");
    }
}
