package ages.hopeful.factories;

import ages.hopeful.modules.scenarios.model.Parameter;
import ages.hopeful.modules.scenarios.model.Scenario;

/**
 * Factory para criar instâncias de Parameter para testes.
 */
public class ParameterFactory {

    public static Parameter createParameter() {
        return createParameter("Nível da água > 1m", "Acionar alerta", "Resposta");
    }

    public static Parameter createParameter(String description, String action, String phase) {
        return Parameter.builder()
                .description(description)
                .action(action)
                .phase(phase)
                .build();
    }

    public static Parameter createParameterWithScenario(Scenario scenario) {
        return Parameter.builder()
                .description("Parâmetro vinculado ao cenário")
                .action("Ação de teste")
                .phase("Resposta")
                .scenario(scenario)
                .build();
    }

    public static Parameter createWaterLevelParameter() {
        return createParameter(
            "Nível da água > 2m",
            "Evacuação imediata",
            "Resposta"
        );
    }

    public static Parameter createWindSpeedParameter() {
        return createParameter(
            "Velocidade do vento > 80km/h",
            "Suspender operações externas",
            "Resposta"
        );
    }

    public static Parameter createRainfallParameter() {
        return createParameter(
            "Precipitação > 50mm/h",
            "Alerta máximo",
            "Preparação"
        );
    }

    public static Parameter createFireParameter() {
        return createParameter(
            "Área queimada > 100 hectares",
            "Acionar reforços",
            "Resposta"
        );
    }

    public static Parameter createDisplacedPeopleParameter() {
        return createParameter(
            "Número de desabrigados > 500",
            "Ativar plano de contingência",
            "Resposta"
        );
    }
}
