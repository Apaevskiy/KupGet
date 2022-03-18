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
    private final TrafficPersonRepository personRepository;
    private final TrafficItemTypeRepository typeRepository;
    private final TrafficTeamRepository teamRepository;
    private final TrafficVehicleRepository vehicleRepository;

    public Flux<TrafficItemType> getItemsType() {
        return Flux.fromIterable(typeRepository.findAll());
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return Mono.just(typeRepository.save(type));
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return Mono.just(teamRepository.save(trafficTeam));
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return Mono.just(vehicleRepository.save(trafficVehicle));
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

    public Mono<TrafficPerson> saveTrafficPerson(TrafficPerson trafficPerson) {
        return Mono.just(personRepository.save(trafficPerson));
    }

    public Flux<TrafficPerson> getTrafficPeople() {
        return Flux.fromIterable(personRepository.findAll());
    }

    public Flux<TrafficItem> getTrafficItems() {
        return Flux.fromIterable(itemRepository.findAll());
    }

    public Flux<TrafficPerson> getPeopleByTeam(TrafficTeam team) {
        return Flux.fromIterable(personRepository.findAllByTeamId(team.getId()));
    }

    public Mono<TrafficItem> saveTrafficItem(TrafficItem trafficItem) {
        if(trafficItem.getPerson()!=null && trafficItem.getPerson().getPersonId()!=null){
            TrafficPerson person = personRepository.findFirstByPersonId(trafficItem.getPerson().getPersonId());
            if(person==null){
                personRepository.save(trafficItem.getPerson());
            }
            trafficItem.setPerson(person);
        }
        return Mono.just(itemRepository.save(trafficItem));
    }

    public Flux<TrafficTeam> saveTrafficTeams(List<TrafficTeam> teams) {
        return Flux.fromIterable(teamRepository.saveAll(teams));
    }

    public Flux<TrafficVehicle> saveTrafficVehicles(List<TrafficVehicle> vehicles) {
        teamRepository.saveAll(vehicles.stream().map(TrafficVehicle::getTeam).filter(Objects::nonNull).collect(Collectors.toList()));
        return Flux.fromIterable(vehicleRepository.saveAll(vehicles));
    }

    public Flux<TrafficItemType> saveItemTypes(List<TrafficItemType> types) {
        return Flux.fromIterable(typeRepository.saveAll(types));
    }
}
