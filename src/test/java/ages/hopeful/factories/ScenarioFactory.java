package ages.hopeful.factories;

import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.model.Task;
import ages.hopeful.modules.scenarios.model.Parameter;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Factory para criar instâncias de Scenario para testes.
 */
public class ScenarioFactory {

    public static Scenario createScenario() {
        return createScenario("Manual");
    }

    public static Scenario createScenario(String origin) {
        Scenario scenario = Scenario.builder()
                .origin(origin)
                .city(CityFactory.createCity())
                .cobrade(CobradeFactory.createInundacao())
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .published(false)
                .build();
        
        return scenario;
    }

    public static Scenario createScenarioWithId(UUID id) {
        Scenario scenario = createScenario();
        scenario.setId(id);
        return scenario;
    }

    public static Scenario createPublishedScenario() {
        Scenario scenario = createScenario("Manual");
        scenario.setPublished(true);
        return scenario;
    }

    public static Scenario createScenarioWithTasks() {
        Scenario scenario = createScenario();
        
        Task task1 = TaskFactory.createEvacuationTask();
        task1.setScenario(scenario);
        
        Task task2 = TaskFactory.createRescueTask();
        task2.setScenario(scenario);
        
        scenario.getTasks().add(task1);
        scenario.getTasks().add(task2);
        
        return scenario;
    }

    public static Scenario createScenarioWithParameters() {
        Scenario scenario = createScenario();
        
        Parameter param1 = ParameterFactory.createWaterLevelParameter();
        param1.setScenario(scenario);
        
        Parameter param2 = ParameterFactory.createRainfallParameter();
        param2.setScenario(scenario);
        
        scenario.getParameters().add(param1);
        scenario.getParameters().add(param2);
        
        return scenario;
    }

    public static Scenario createCompleteScenario() {
        Scenario scenario = Scenario.builder()
                .origin("Sistema de monitoramento")
                .city(CityFactory.createFlorianopolis())
                .cobrade(CobradeFactory.createInundacao())
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .published(false)
                .build();
        
        Task task1 = TaskFactory.createEvacuationTask();
        task1.setScenario(scenario);
        scenario.getTasks().add(task1);
        
        Task task2 = TaskFactory.createMonitoringTask();
        task2.setScenario(scenario);
        scenario.getTasks().add(task2);
        
        Parameter param1 = ParameterFactory.createWaterLevelParameter();
        param1.setScenario(scenario);
        scenario.getParameters().add(param1);
        
        Parameter param2 = ParameterFactory.createDisplacedPeopleParameter();
        param2.setScenario(scenario);
        scenario.getParameters().add(param2);
        
        return scenario;
    }

    public static Scenario createFloodScenario() {
        return Scenario.builder()
                .origin("Chuva intensa")
                .city(CityFactory.createFlorianopolis())
                .cobrade(CobradeFactory.createInundacao())
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .published(false)
                .build();
    }

    public static Scenario createStormScenario() {
        return Scenario.builder()
                .origin("Frente fria intensa")
                .city(CityFactory.createRioDeJaneiro())
                .cobrade(CobradeFactory.createVendaval())
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .published(false)
                .build();
    }

    public static Scenario createFireScenario() {
        return Scenario.builder()
                .origin("Período de seca")
                .city(CityFactory.createSaoPaulo())
                .cobrade(CobradeFactory.createIncendioFlorestal())
                .tasks(new ArrayList<>())
                .parameters(new ArrayList<>())
                .published(false)
                .build();
    }
}
