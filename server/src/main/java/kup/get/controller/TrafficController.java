package kup.get.controller;

import kup.get.entity.alfa.traffic.TrafficTeam;
import kup.get.entity.alfa.traffic.TrafficVehicle;
import kup.get.entity.postgres.traffic.*;
import kup.get.service.traffic.TrafficItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
public class TrafficController {
    private final TrafficItemService trafficItemService;



    @GetMapping("/traffic/trafficItemType")
    Flux<TrafficItemType> getTrafficItemType() {
        return Flux.fromIterable(trafficItemService.getAllItemTypes());
    }

    @PostMapping("/traffic/saveItemType")
    Mono<TrafficItemType> saveItemsTypes(@RequestBody Mono<TrafficItemType> itemType) {
        return itemType.map(trafficItemService::saveType);
    }


    @PostMapping("/traffic/saveTrafficTeam")
    Mono<TrafficTeam> saveTrafficTeam(@RequestBody Mono<TrafficTeam> trafficTeam) {
        return trafficTeam.map(trafficItemService::saveTrafficTeam);
    }

    @GetMapping("/traffic/getTrafficTeam")
    Flux<TrafficTeam> getTrafficTeam() {
        return Flux.fromIterable(trafficItemService.getAllTrafficTeam());
    }

    @DeleteMapping("/traffic/deleteTrafficTeam/{id}")
    Mono<Void> deleteTrafficTeam(@PathVariable("id") Long id) {
        trafficItemService.deleteTrafficTeam(id);
        return Mono.empty();
    }

    @GetMapping("/traffic/getTrafficVehicle")
    Flux<TrafficVehicle> getTrafficVehicle() {
        return Flux.fromIterable(trafficItemService.getTrafficVehicle());
    }

    @PostMapping("/traffic/saveTrafficVehicle")
    Mono<TrafficVehicle> saveTrafficVehicle(@RequestBody Mono<TrafficVehicle> trafficVehicle) {
        return trafficVehicle.map(trafficItemService::saveTrafficVehicle);
    }

    @DeleteMapping("/traffic/deleteTrafficVehicle/{id}")
    Mono<Void> deleteTrafficVehicle(@PathVariable("id") Long id) {
        trafficItemService.deleteTrafficVehicle(id);
        return Mono.empty();
    }

    @GetMapping("/traffic/getTrafficItems")
    Flux<TrafficItem> getTrafficItems() {
        return Flux.fromIterable(trafficItemService.getTrafficItems());
    }

    @PostMapping("/traffic/saveTrafficItem")
    Mono<TrafficItem> saveTrafficItem(@RequestBody Mono<TrafficItem> itemMono) {
        return itemMono.map(trafficItemService::saveTrafficItem);
    }

    @PostMapping("/traffic/saveTrafficTeams")
    Flux<TrafficTeam> saveTrafficTeams(@RequestBody Flux<TrafficTeam> teamFlux) {
        return teamFlux.map(trafficItemService::saveTrafficTeam);
    }

    @PostMapping("/traffic/saveTrafficVehicles")
    Flux<TrafficVehicle> saveTrafficVehicles(@RequestBody Flux<TrafficVehicle> vehicleFlux) {
        return vehicleFlux.map(trafficItemService::saveTrafficVehicle);
    }

    @PostMapping("/traffic/saveItemTypes")
    Flux<TrafficItemType> saveItemTypes(@RequestBody Flux<TrafficItemType> typeFlux) {
        return typeFlux.map(trafficItemService::saveType);
    }
}
