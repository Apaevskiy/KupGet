package kup.get.service.socket;

import kup.get.entity.traffic.*;
import kup.get.service.MyTrafficService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class TrafficSocketService implements MyTrafficService {
    private final SocketService socketService;

    public Flux<TrafficItemType> getItemsType() {
        return socketService.getClient().get()
                .uri("/traffic/trafficItemType")
                .retrieve()
                .bodyToFlux(TrafficItemType.class);
    }

    public Mono<TrafficItemType> saveItemType(TrafficItemType type) {
        return socketService.getClient().post().uri("/traffic/saveItemType")
                .bodyValue(type)
                .retrieve().bodyToMono(TrafficItemType.class);
    }

    public Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam) {
        return socketService.getClient().post().uri("/traffic/saveTrafficTeam")
                .bodyValue(trafficTeam)
                .retrieve().bodyToMono(TrafficTeam.class);
    }

    public Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle) {
        return socketService.getClient().post().uri("/traffic/saveTrafficVehicle")
                .bodyValue(trafficVehicle)
                .retrieve().bodyToMono(TrafficVehicle.class);
    }

    public Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle) {
        return socketService.getClient().delete()
                .uri("/traffic/deleteTrafficVehicle/{id}", vehicle.getId())
                .retrieve().bodyToMono(Void.class);
    }

    public Flux<TrafficVehicle> getTrafficVehicle() {
        return socketService.getClient().get().uri("/traffic/getTrafficVehicle")
                .retrieve().bodyToFlux(TrafficVehicle.class);
    }

    public Flux<TrafficTeam> getAllTrafficTeam() {
        return socketService.getClient().get().uri("/traffic/getTrafficTeam")
                .retrieve().bodyToFlux(TrafficTeam.class);
    }

    public Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam) {
        return socketService.getClient().delete()
                .uri("/traffic/deleteTrafficTeam/{id}", trafficTeam.getId())
                .retrieve().bodyToMono(Void.class);
    }

    public Flux<TrafficItem> getTrafficItems() {
        return socketService.getClient().get().uri("/traffic/getTrafficItems")
                .retrieve().bodyToFlux(TrafficItem.class);
    }

    public Mono<TrafficItem> saveTrafficItem(TrafficItem item) {
        return socketService.getClient().post().uri("/traffic/saveTrafficItem")
                .bodyValue(item)
                .retrieve().bodyToMono(TrafficItem.class);
    }

    @Deprecated
    public Flux<TrafficTeam> saveTrafficTeams(List<TrafficTeam> teams) {
        return socketService.getClient().post().uri("/traffic/saveTrafficTeams")
                .bodyValue(Flux.fromIterable(teams))
                .retrieve().bodyToFlux(TrafficTeam.class);
    }

    @Deprecated
    public Flux<TrafficVehicle> saveTrafficVehicles(List<TrafficVehicle> vehicles) {
        return socketService.getClient().post().uri("/traffic/saveTrafficVehicles")
                .bodyValue(Flux.fromIterable(vehicles))
                .retrieve().bodyToFlux(TrafficVehicle.class);
    }

    @Deprecated
    public Flux<TrafficItemType> saveItemTypes(List<TrafficItemType> types) {
        return socketService.getClient().post().uri("/traffic/saveItemTypes")
                .bodyValue(Flux.fromIterable(types))
                .retrieve().bodyToFlux(TrafficItemType.class);
    }
}
