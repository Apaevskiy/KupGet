package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.postgres.traffic.TrafficItemType;
import kup.get.entity.postgres.traffic.TrafficPerson;
import kup.get.entity.postgres.traffic.TrafficTeam;
import kup.get.entity.postgres.traffic.TrafficVehicle;
import kup.get.service.alfa.AlfaService;
import kup.get.service.traffic.TrafficItemService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
public class TrafficController {
    private final TrafficItemService trafficItemService;
    private final AlfaService alfaService;

    @MessageMapping("briefing")
    Flux<TrafficPerson> getBriefing(Mono<LocalDate> dateMono) {
        return dateMono
                .flatMapMany(date -> Flux.fromIterable(trafficItemService.getBriefingOfPeople(date)));
    }

    @MessageMapping("drivers")
    Flux<Person> getDrivers() {
        return Flux.fromIterable(alfaService.getDrivers());
    }

    @MessageMapping("greetings")
    Flux<String> greet(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user
                .flatMapMany(u ->
                        Flux.fromIterable(u.getAuthorities()
                                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
    }

    @MessageMapping("traffic.trafficItemType")
    Flux<TrafficItemType> getTrafficItemType() {
        System.out.println("HI");
        return Flux.fromIterable(trafficItemService.getAllItemTypes());
    }

    @MessageMapping("saveItemsTypes")
    Mono<Boolean> saveItemsTypes(Flux<TrafficItemType> flux) {
        return flux
                .collect(Collectors.toList())
                .doOnNext(trafficItemService::saveType)
                .map(e -> true)
                .onErrorReturn(false);
    }
    @MessageMapping("traffic.saveTrafficPerson")
    Mono<TrafficPerson> saveTrafficPerson(Mono<TrafficPerson> person) {
        return person
                .map(trafficItemService::saveTrafficPerson);
    }

    @MessageMapping("traffic.saveTrafficTeam")
    Mono<TrafficTeam> saveTrafficTeam(Mono<TrafficTeam> trafficTeam) {
        return trafficTeam
                .map(trafficItemService::saveTrafficTeam);
    }
    @MessageMapping("traffic.getTrafficTeam")
    Flux<TrafficTeam> getTrafficTeam() {
        return Flux.fromIterable(trafficItemService.getAllTrafficTeam());
    }
    @MessageMapping("traffic.deleteTrafficTeam")
    Mono<Void> deleteTrafficTeam(Mono<TrafficTeam> trafficVehicle) {
        return trafficVehicle
                .doOnNext(trafficItemService::deleteTrafficTeam)
                .then(Mono.empty());
    }
    @MessageMapping("traffic.getTrafficVehicle")
    Flux<TrafficVehicle> getTrafficVehicle() {
        return Flux.fromIterable(trafficItemService.getTrafficVehicle());
    }
    @MessageMapping("traffic.saveTrafficVehicle")
    Mono<TrafficVehicle> saveTrafficVehicle(Mono<TrafficVehicle> trafficVehicle) {
        return trafficVehicle
                .map(trafficItemService::saveTrafficVehicle);
    }
    @MessageMapping("traffic.deleteTrafficVehicle")
    Mono<Void> deleteTrafficVehicle(Mono<TrafficVehicle> trafficVehicle) {
        return trafficVehicle
                .doOnNext(trafficItemService::deleteTrafficVehicle)
                .then(Mono.empty());
    }
}
