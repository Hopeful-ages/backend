package ages.hopeful.factories;

import ages.hopeful.modules.city.model.City;

import java.util.UUID;

/**
 * Factory para criar instâncias de City para testes.
 */
public class CityFactory {

    public static City createCity() {
        return createCity("Porto Alegre", "Rio Grande do Sul");
    }

    public static City createCity(String name, String state) {
        City city = new City();
        city.setName(name);
        city.setState(state);
        return city;
    }

    public static City createCityWithId(UUID id, String name, String state) {
        City city = createCity(name, state);
        city.setId(id);
        return city;
    }

    public static City createFlorianopolis() {
        return createCity("Florianópolis", "Santa Catarina");
    }

    public static City createSaoPaulo() {
        return createCity("São Paulo", "São Paulo");
    }

    public static City createRioDeJaneiro() {
        return createCity("Rio de Janeiro", "Rio de Janeiro");
    }
}
