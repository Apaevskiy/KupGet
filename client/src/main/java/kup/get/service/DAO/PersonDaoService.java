package kup.get.service.DAO;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.repository.alfa.DepartmentRepository;
import kup.get.repository.alfa.PersonRepository;
import kup.get.repository.alfa.PhotoRepository;
import kup.get.repository.alfa.PositionRepository;
import kup.get.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonDaoService implements PersonService {
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    private final PhotoRepository photoRepository;
    private final List<Person> people = new ArrayList<>();
    public void updatePeople() {
        people.clear();
        people.addAll(personRepository.findAll(/*String.valueOf(LocalDate.now())*/));
    }

    public List<Person> getPeople() {
        return people;
    }

    public Mono<Person> savePerson(Person person) {
        return null;
    }

    public Mono<byte[]> getPhotoByPerson(Long id) {
        Photo photo = photoRepository.findFirstByPersonId(id);
        return photo != null? Mono.just( photo.getPhoto()) : Mono.empty();
    }

    public Flux<Person> savePeople(List<Person> people) {
        departmentRepository.saveAll(people.stream().map(Person::getDepartment).filter(Objects::nonNull).collect(Collectors.toList()));
        positionRepository.saveAll(people.stream().map(Person::getPosition).filter(Objects::nonNull).collect(Collectors.toList()));
        return Flux.fromIterable(personRepository.saveAll(people));
    }

    public Flux<Photo> addPhotoToPeople() {
        return Flux.fromIterable(photoRepository.findAll());
    }
}
