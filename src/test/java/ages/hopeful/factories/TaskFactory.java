package ages.hopeful.factories;

import ages.hopeful.modules.scenarios.model.Task;
import ages.hopeful.modules.scenarios.model.Scenario;

import java.util.Date;

/**
 * Factory para criar instâncias de Task para testes.
 */
public class TaskFactory {

    public static Task createTask() {
        return createTask("Tarefa de teste", "Resposta");
    }

    public static Task createTask(String description, String phase) {
        return Task.builder()
                .description(description)
                .phase(phase)
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createTaskWithDepartment(String description, String phase) {
        return Task.builder()
                .description(description)
                .phase(phase)
                .department(DepartmentFactory.createDefesaCivil())
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createTaskWithScenario(Scenario scenario) {
        return Task.builder()
                .description("Tarefa vinculada ao cenário")
                .phase("Resposta")
                .scenario(scenario)
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createEvacuationTask() {
        return Task.builder()
                .description("Evacuação de moradores")
                .phase("Resposta")
                .department(DepartmentFactory.createDefesaCivil())
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createRescueTask() {
        return Task.builder()
                .description("Resgate em área alagada")
                .phase("Resposta")
                .department(DepartmentFactory.createBombeiros())
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createMonitoringTask() {
        return Task.builder()
                .description("Monitoramento de níveis")
                .phase("Preparação")
                .lastUpdateDate(new Date())
                .build();
    }

    public static Task createRecoveryTask() {
        return Task.builder()
                .description("Limpeza de vias públicas")
                .phase("Recuperação")
                .department(DepartmentFactory.createObras())
                .lastUpdateDate(new Date())
                .build();
    }
}
