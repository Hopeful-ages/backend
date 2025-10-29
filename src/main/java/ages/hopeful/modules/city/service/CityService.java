package ages.hopeful.modules.city.service;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ages.hopeful.modules.city.dto.CityResponseDTO;
import ages.hopeful.modules.city.model.City;
import ages.hopeful.modules.city.repository.CityRepository;

@Service
@AllArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public List<CityResponseDTO> getAllCities() {
        List<City> cities = cityRepository.findAll();
        return cities.stream()
                .map(city -> new CityResponseDTO(city.getId(), city.getName(), city.getState()))
                .toList();
    }

    public City getCityById(UUID id){
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));
    }

}
