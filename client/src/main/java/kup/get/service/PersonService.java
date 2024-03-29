package kup.get.service;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonService {
    Flux<Person> getPeople();
    Mono<byte[]> getPhotoByPerson(Long id);

    Flux<Person> savePeople(List<Person> people);

    Flux<Photo> addPhotoToPeople();
    Flux<Photo> savePhotos(List<Photo> photos);
}
