package kup.get.service.socket;

import kup.get.config.RSocketClientBuilderImpl;
import kup.get.entity.traffic.*;
import kup.get.service.DAO.PersonDaoService;
import kup.get.service.MyTrafficService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TrafficSocketService extends SocketService implements MyTrafficService {
    private final PersonDaoService personDaoService;
    private final RSocketClientBuilderImpl config;
    public TrafficSocketService(RSocketClientBuilderImpl config, PersonDaoService personDaoService) {
        super(config);
        this.config = config;
        this.personDaoService = personDaoService;
    }

    public Flux<TrafficItemType> getItemsType() {
        return route("traffic.trafficItemType")
                .retrieveFlux(TrafficItemType.class);
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return route("traffic.saveItemType")
                .data(type)
                .retrieveMono(TrafficItemType.class);
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return route("traffic.saveTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(TrafficTeam.class);
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return route("traffic.saveTrafficVehicle")
                .data(trafficVehicle)
                .retrieveMono(TrafficVehicle.class);
    }

    public Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle) {
        return route("traffic.deleteTrafficVehicle")
                .data(vehicle)
                .retrieveMono(Void.class);
    }

    public Flux<TrafficVehicle> getTrafficVehicle() {
        return route("traffic.getTrafficVehicle")
                .retrieveFlux(TrafficVehicle.class);
    }

    public Flux<TrafficTeam> getAllTrafficTeam() {
        return route("traffic.getTrafficTeam")
                .retrieveFlux(TrafficTeam.class);
    }

    public Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam) {
        return route("traffic.deleteTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(Void.class);
    }

    public Mono<TrafficPerson> saveTrafficPerson(TrafficPerson person) {
        return route("traffic.saveTrafficPerson")
                .data(person)
                .retrieveMono(TrafficPerson.class);
    }

    public Flux<TrafficPerson> getTrafficPeople() {
        return route("traffic.getTrafficPeople")
                .retrieveFlux(TrafficPerson.class);
    }

    public Flux<TrafficItem> getTrafficItems() {
        return route("traffic.getTrafficItems")
                .retrieveFlux(TrafficItem.class);
    }

    public Flux<TrafficPerson> getPeopleByTeam(TrafficTeam team) {
        return route("traffic.getPeopleByTeam")
                .data(team.getId())
                .retrieveFlux(TrafficPerson.class);
    }

    public Mono<TrafficItem> saveTrafficItem(TrafficItem item) {
        return route("traffic.saveTrafficItem")
                .data(item)
                .retrieveMono(TrafficItem.class);
    }
}
