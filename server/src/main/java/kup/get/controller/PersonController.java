package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.service.alfa.AlfaService;
import kup.get.service.traffic.TrafficItemService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Controller
public class PersonController {
    private final TrafficItemService trafficItemService;
    private final AlfaService alfaService;

    @MessageMapping("auth.people")
    Flux<Person> getDrivers() {
        return Flux.fromIterable(alfaService.getPeople());
    }


    @MessageMapping("auth.getPhotoByPerson")
    Mono<byte[]> getPhotoByPerson(Mono<Long> idMono) {
        return idMono.map(alfaService::getPhotoByPerson);
    }

    @MessageMapping("auth.getPhotoByPeople")
    Flux<Photo> getPhotoByPeople() {
        return Flux.fromIterable(alfaService.getPhotoByPeople());
    }
}
