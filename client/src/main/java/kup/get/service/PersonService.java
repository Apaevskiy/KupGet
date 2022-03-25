package kup.get.service;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.entity.security.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonService {
    void updatePeople();
    List<Person> getPeople();
    Mono<Person> savePerson(Person person);
    Mono<byte[]> getPhotoByPerson(Long id);

    Flux<Person> savePeople(List<Person> people);

    Flux<Photo> addPhotoToPeople();


}
