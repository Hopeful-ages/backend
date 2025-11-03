package ages.hopeful.modules.city.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponseDTO {
    private UUID id;
    private String name;
    private String state;


    public static CityResponseDTO fromModel(ages.hopeful.modules.city.model.City city) {
        if (city == null) return null;
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .state(city.getState())
                .build();
    }
}