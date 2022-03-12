package kup.get.service.DAO;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.repository.alfa.PersonRepository;
import kup.get.repository.alfa.PhotoRepository;
import kup.get.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PersonDaoService implements PersonService {
    private final PersonRepository personRepository;
    private final PhotoRepository photoRepository;
    private final List<Person> people = new ArrayList<>();
    public void updatePeople() {
        people.clear();
        people.addAll(personRepository.findAllWorkPeople(String.valueOf(LocalDate.now())));
    }

    public List<Person> getPeople() {
        return people;
    }

    public Mono<Person> savePerson(Person person) {
        return null;
    }

    public byte[] getPhotoByPerson(Long id) {
        Photo photo = photoRepository.findFirstByPersonId(id);
        return photo != null ? photo.getPhoto() : null;
    }
}
