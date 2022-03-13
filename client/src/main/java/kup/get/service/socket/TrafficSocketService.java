package kup.get.service.socket;

import kup.get.entity.traffic.*;
import kup.get.service.MyTrafficService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TrafficSocketService implements MyTrafficService {
    private final SocketService socketService;

    public Flux<TrafficItemType> getItemsType() {
        return socketService.route("traffic.trafficItemType")
                .retrieveFlux(TrafficItemType.class);
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return socketService.route("traffic.saveItemType")
                .data(type)
                .retrieveMono(TrafficItemType.class);
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return socketService.route("traffic.saveTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(TrafficTeam.class);
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return socketService.route("traffic.saveTrafficVehicle")
                .data(trafficVehicle)
                .retrieveMono(TrafficVehicle.class);
    }

    public Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle) {
        return socketService.route("traffic.deleteTrafficVehicle")
                .data(vehicle)
                .retrieveMono(Void.class);
    }

    public Flux<TrafficVehicle> getTrafficVehicle() {
        return socketService.route("traffic.getTrafficVehicle")
                .retrieveFlux(TrafficVehicle.class);
    }

    public Flux<TrafficTeam> getAllTrafficTeam() {
        return socketService.route("traffic.getTrafficTeam")
                .retrieveFlux(TrafficTeam.class);
    }

    public Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam) {
        return socketService.route("traffic.deleteTrafficTeam")
                .data(trafficTeam)
                .retrieveMono(Void.class);
    }

    public Mono<TrafficPerson> saveTrafficPerson(TrafficPerson person) {
        return socketService.route("traffic.saveTrafficPerson")
                .data(person)
                .retrieveMono(TrafficPerson.class);
    }

    public Flux<TrafficPerson> getTrafficPeople() {
        return socketService.route("traffic.getTrafficPeople")
                .retrieveFlux(TrafficPerson.class);
    }

    public Flux<TrafficItem> getTrafficItems() {
        return socketService.route("traffic.getTrafficItems")
                .retrieveFlux(TrafficItem.class);
    }

    public Flux<TrafficPerson> getPeopleByTeam(TrafficTeam team) {
        return socketService.route("traffic.getPeopleByTeam")
                .data(team.getId())
                .retrieveFlux(TrafficPerson.class);
    }

    public Mono<TrafficItem> saveTrafficItem(TrafficItem item) {
        return socketService.route("traffic.saveTrafficItem")
                .data(item)
                .retrieveMono(TrafficItem.class);
    }
}
