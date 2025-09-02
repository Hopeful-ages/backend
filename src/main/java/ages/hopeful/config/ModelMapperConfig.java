package ages.hopeful.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Estratégia de matching (LOOSE é mais flexível, STRICT é mais rígida)
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.STRICT) 
                   .setSkipNullEnabled(true)   // ignora atributos nulos no mapeamento
                   .setFieldMatchingEnabled(true) // permite matching direto em fields
                   .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }
}
