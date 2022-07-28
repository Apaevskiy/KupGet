package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.service.alfa.AlfaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class PersonController {
    private final AlfaService alfaService;

    @GetMapping("/auth/people")
    Flux<Person> getPeople() {
        return Flux.fromIterable(alfaService.getPeople());
    }


    @GetMapping("/auth/getPhotoByPerson/{id}")
    byte[] getPhotoByPerson(@PathVariable("id") long id) {
        return alfaService.getPhotoByPerson(id);
    }

    @GetMapping("/auth/getPhotoByPeople")
    Flux<Photo> getPhotoByPeople() {
        return Flux.fromIterable(alfaService.getPhotoByPeople());
    }
}
