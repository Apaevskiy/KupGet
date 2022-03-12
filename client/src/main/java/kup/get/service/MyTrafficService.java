package kup.get.service;

import kup.get.entity.traffic.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyTrafficService {
    Flux<TrafficItemType> getItemsType();

    Mono<TrafficItemType> saveItemType(TrafficItemType type);

    Mono<TrafficTeam> saveTrafficTeam(TrafficTeam trafficTeam);

    Mono<TrafficVehicle> saveTrafficVehicle(TrafficVehicle trafficVehicle);

    Mono<Void> deleteTrafficVehicle(TrafficVehicle vehicle);

    Flux<TrafficVehicle> getTrafficVehicle();

    Flux<TrafficTeam> getAllTrafficTeam();

    Mono<Void> deleteTrafficTeam(TrafficTeam trafficTeam);

    Mono<TrafficPerson> saveTrafficPerson(TrafficPerson person);

    Flux<TrafficPerson> getTrafficPeople();

    Flux<TrafficItem> getTrafficItems();

    Flux<TrafficPerson> getPeopleByTeam(TrafficTeam team);

    Mono<TrafficItem> saveTrafficItem(TrafficItem item);
}
