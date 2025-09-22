package ages.hopeful.modules.scenarios.dto;

import java.util.List;
import java.util.UUID;

import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.scenarios.model.Cobrade;
import ages.hopeful.modules.scenarios.model.Scenario;
import ages.hopeful.modules.scenarios.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioByUserRequestDTO {
    private String description;
    private String origin;
    private UUID cityId;
    private UUID cobradeId;
    private List<TaskRequestDTO> tasks;

    public Scenario toModel(City city, Cobrade cobrade, List<Task> tasks) {
    Scenario scenario = new Scenario();
    scenario.setOrigin(this.getOrigin());
    scenario.setCity(city);
    scenario.setCobrade(cobrade);
    scenario.setTasks(tasks);
    return scenario;
}
}
