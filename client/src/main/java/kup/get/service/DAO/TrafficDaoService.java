package kup.get.service.DAO;

import kup.get.entity.traffic.*;
import kup.get.repository.traffic.*;
import kup.get.service.MyTrafficService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrafficDaoService implements MyTrafficService {
    private final TrafficItemRepository itemRepository;
    private final TrafficItemTypeRepository typeRepository;
    private final TrafficTeamRepository teamRepository;
    private final TrafficVehicleRepository vehicleRepository;

    public Flux<TrafficItemType> getItemsType() {
        return Flux.fromIterable(typeRepository.findAll());
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return Mono.just(type).map(typeRepository::save);
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return Mono.just(trafficTeam).map(teamRepository::save);
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return Mono.just(trafficVehicle).map(vehicleRepository::save);
    }

    public Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle) {
        vehicleRepository.delete(vehicle);
        return Mono.empty();
    }

    public Flux<TrafficVehicle> getTrafficVehicle() {
        return Flux.fromIterable(vehicleRepository.findAll());
    }

    public Flux<TrafficTeam> getAllTrafficTeam() {
        return Flux.fromIterable(teamRepository.findAll());
    }

    public Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam) {
        teamRepository.delete(trafficTeam);
        return Mono.empty();
    }

    public Flux<TrafficItem> getTrafficItems() {
        return Flux.fromIterable(itemRepository.findAll());
    }

    public Mono<TrafficItem> saveTrafficItem(TrafficItem trafficItem) {
        /*if(trafficItem.getPerson()!=null && trafficItem.getPerson().getPersonId()!=null){
            TrafficPerson person = personRepository.findFirstByPersonId(trafficItem.getPerson().getPersonId());
            if(person==null){
                personRepository.save(trafficItem.getPerson());
            }
            trafficItem.setPerson(person);
        }*/
        return Mono.just(trafficItem).map(itemRepository::save);
    }

    public Flux<TrafficTeam> saveTrafficTeams(List<TrafficTeam> teams) {
        return Flux.fromIterable(teams).map(teamRepository::save);
    }

    public Flux<TrafficVehicle> saveTrafficVehicles(List<TrafficVehicle> vehicles) {
//        teamRepository.saveAll(vehicles.stream().map(TrafficVehicle::getTeam).filter(Objects::nonNull).collect(Collectors.toList()));
        return Flux.fromIterable(vehicleRepository.saveAll(vehicles));
    }

    public Flux<TrafficItemType> saveItemTypes(List<TrafficItemType> types) {
        return Flux.fromIterable(typeRepository.saveAll(types));
    }
}
