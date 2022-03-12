package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.postgres.traffic.*;
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

    @MessageMapping("greetings")
    Flux<String> greet(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user
                .flatMapMany(u ->
                        Flux.fromIterable(u.getAuthorities()
                                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
    }

    @MessageMapping("traffic.briefing")
    Flux<TrafficPerson> getBriefing(Mono<LocalDate> dateMono) {
        return dateMono
                .flatMapMany(date -> Flux.fromIterable(trafficItemService.getBriefingOfPeople(date)));
    }
    @MessageMapping("traffic.people")
    Flux<Person> getDrivers() {
        return Flux.fromIterable(alfaService.getPeople());
    }
    @MessageMapping("getPhotoByPerson")
    Mono<byte[]> getPhotoByPerson(Mono<Long> idMono) {
        return idMono.map(alfaService::getPhotoByPerson);
    }

    @MessageMapping("traffic.getPeopleByTeam")
    Flux<TrafficPerson> getPeopleByTeam(Mono<Long> id) {
        return id.flatMapMany(l -> Flux.fromIterable(trafficItemService.getTrafficPeopleByTeam(l)));
    }

    @MessageMapping("traffic.trafficItemType")
    Flux<TrafficItemType> getTrafficItemType() {
        return Flux.fromIterable(trafficItemService.getAllItemTypes());
    }

    @MessageMapping("traffic.saveItemType")
    Mono<TrafficItemType> saveItemsTypes(Mono<TrafficItemType> itemType) {
        return itemType
                .map(trafficItemService::saveType);
    }
    @MessageMapping("traffic.saveTrafficPerson")
    Mono<TrafficPerson> saveTrafficPerson(Mono<TrafficPerson> person) {
        return person
                .map(trafficItemService::saveTrafficPerson);
    }
    @MessageMapping("traffic.getTrafficPeople")
    Flux<TrafficPerson> getTrafficPeople() {
        return Flux.fromIterable(trafficItemService.getTrafficPeople());
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
    @MessageMapping("traffic.getTrafficItems")
    Flux<TrafficItem> getTrafficItems() {
        return Flux.fromIterable(trafficItemService.getTrafficItems());
    }
    @MessageMapping("traffic.saveTrafficItem")
    Mono<TrafficItem> saveTrafficItem(Mono<TrafficItem> itemMono){
        return itemMono.map(trafficItemService::saveTrafficItem);
    }
}
