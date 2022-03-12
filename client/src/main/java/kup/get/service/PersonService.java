package kup.get.service;

import kup.get.entity.alfa.Person;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonService {
    void updatePeople();
    List<Person> getPeople();
    Mono<Person> savePerson(Person person);
    byte[] getPhotoByPerson(Long id);
}
