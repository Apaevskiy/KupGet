package kup.get.service.alfa;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.repository.alfa.PersonRepository;
import kup.get.repository.alfa.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AlfaService {
    private final PersonRepository personRepository;
    private final PhotoRepository photoRepository;

    public List<Person> getPeople() {
        return personRepository.findAllWorkPeople(String.valueOf(LocalDate.now()));
    }

    public byte[] getPhotoByPerson(long id) {
        Photo photo = photoRepository.findFirstByPersonId(id);
        return photo != null ? photo.getPhoto() : null;
    }
    public List<Photo> getPhotoByPeople() {
        return photoRepository.findAllByIdIn(String.valueOf(LocalDate.now()));
    }

    public Person savePerson(Person p) {
        return personRepository.save(p);
    }
}
