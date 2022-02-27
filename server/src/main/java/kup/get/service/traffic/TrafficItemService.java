package kup.get.service.traffic;

import kup.get.entity.postgres.traffic.*;
import kup.get.repository.postgres.traffic.*;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TrafficItemService {
    private final TrafficItemRepository itemRepository;
    private final TrafficPersonRepository personRepository;
    private final TrafficItemTypeRepository typeRepository;
    private final TrafficTeamRepository teamRepository;
    private final TrafficVehicleRepository vehicleRepository;

    public List<TrafficPerson> getBriefingOfPeople(LocalDate date) {
//        Long id = typeRepository.findFirstByName("Инструктажи по охране труда").getId();
        return personRepository.findAllByItemsTypeIdAndItemsDateFinishAfter(1L, date);
    }

    public TrafficItemType saveType(TrafficItemType type) {
        System.out.println(type);
        return typeRepository.save(type);
    }

    public List<TrafficItemType> getAllItemTypes() {
        return typeRepository.findAll();
    }

    public TrafficTeam saveTrafficTeam(TrafficTeam trafficTeam) {
        return teamRepository.save(trafficTeam);
    }

    public List<TrafficVehicle> getTrafficVehicle(){
        return vehicleRepository.findAll();
    }
    public TrafficVehicle saveTrafficVehicle(TrafficVehicle tv) {
        return vehicleRepository.save(tv);
    }
    public void deleteTrafficVehicle(TrafficVehicle tv){
        vehicleRepository.delete(tv);
    }

    public List<TrafficTeam> getAllTrafficTeam() {
        return teamRepository.findAll();
    }

    public void deleteTrafficTeam(TrafficTeam trafficTeam) {
        teamRepository.delete(trafficTeam);
    }

    public TrafficPerson saveTrafficPerson(TrafficPerson trafficPerson) {
        return personRepository.save(trafficPerson);
    }

    public List<TrafficPerson> getTrafficPeople() {
        return personRepository.findAll();
    }

    public List<TrafficItem> getTrafficItems() {
        return itemRepository.findAll();
    }

    public List<TrafficPerson> getTrafficPeopleByTeam(Long id) {
        return personRepository.findAllByTeamId(id);
    }
}
