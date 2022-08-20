package kup.get.service.traffic;

import kup.get.entity.alfa.traffic.TrafficTeam;
import kup.get.entity.alfa.traffic.TrafficVehicle;
import kup.get.entity.postgres.traffic.*;
import kup.get.repository.alfa.TrafficTeamRepository;
import kup.get.repository.alfa.TrafficVehicleRepository;
import kup.get.repository.postgres.traffic.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@AllArgsConstructor
public class TrafficItemService {
    private final TrafficItemRepository itemRepository;
    private final TrafficItemTypeRepository typeRepository;
    private final TrafficTeamRepository teamRepository;
    private final TrafficVehicleRepository vehicleRepository;

    public TrafficItemType saveType(TrafficItemType type) {
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
    public void deleteTrafficVehicle(Long id){
        vehicleRepository.deleteById(id);
    }

    public List<TrafficTeam> getAllTrafficTeam() {
        return teamRepository.findAll();
    }

    @PostConstruct
    public void test(){
        int i=0;
        for(TrafficTeam team: getAllTrafficTeam()){
            if(i++ > 5)
                break;
            System.out.println(team);
        }
    }

    public void deleteTrafficTeam(Long id) {
        teamRepository.deleteById(id);
    }


    public List<TrafficItem> getTrafficItems() {
        return itemRepository.findAll();
    }

    public TrafficItem saveTrafficItem(TrafficItem trafficItem) {
        System.out.println(trafficItem);
        return itemRepository.saveAndFlush(trafficItem);
    }
}
