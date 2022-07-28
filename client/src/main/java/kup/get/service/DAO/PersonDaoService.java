package kup.get.service.DAO;

import kup.get.entity.alfa.Department;
import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.entity.alfa.Position;
import kup.get.repository.alfa.DepartmentRepository;
import kup.get.repository.alfa.PersonRepository;
import kup.get.repository.alfa.PhotoRepository;
import kup.get.repository.alfa.PositionRepository;
import kup.get.service.PersonService;
import lombok.AllArgsConstructor;
import org.hibernate.engine.jdbc.BinaryStream;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
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

    public Flux<Person> getPeople() {
        return Flux.fromIterable(personRepository.findAll());
    }

    public Person savePerson(Person person) {
        if (person != null) {
            if (person.getPosition() != null) {
                Position position = positionRepository.findFirstById(person.getPosition().getId());
                if (position != null) {
                    position.setName(person.getPosition().getName());
                    person.setPosition(position);
                }
                else position = person.getPosition();
                positionRepository.save(position);
            }
            if (person.getDepartment() != null) {
                Department department = departmentRepository.findFirstById(person.getDepartment().getId());
                if (department != null) {
                    person.setDepartment(department);
                }
                else departmentRepository.save(person.getDepartment());
            }
            if (person.getId() != null) {
                Person p = personRepository.findFirstById(person.getId());
                if (p != null) {
                    person.setLocalId(p.getLocalId());
                    person.setPersonnelNumber(p.getPersonnelNumber());
                    person.setDepartment(p.getDepartment());
                    person.setPosition(p.getPosition());
                    person.setLastName(p.getLastName());
                    person.setFirstName(p.getFirstName());
                    person.setMiddleName(p.getMiddleName());
                    person.setRank(p.getRank());
                }
            }
            return personRepository.save(person);
        }
        return null;
    }

    public Mono<byte[]> getPhotoByPerson(Long id) {
        Photo photo = photoRepository.findFirstByPersonId(id);
        return photo != null ? Mono.just(photo.getPhoto()) : Mono.empty();
    }

    public Flux<Person> savePeople(List<Person> people) {
        return Flux.fromIterable(people).map(this::savePerson);
    }

    public Flux<Photo> addPhotoToPeople() {
        return Flux.fromIterable(photoRepository.findAll());
    }

    //    @Override
    public Flux<Photo> savePhotos(List<Photo> photos) {
//        photoRepository.saveAll(photos);
        return Flux.fromIterable(photos).map(photoRepository::save);
        /*return Flux.fromIterable(photos).publishOn(Schedulers.boundedElastic()).mapNotNull(photo -> {
            try {
                photoRepository.savePhoto(photo.getId(), photo.getPersonId(), new SerialBlob(photo.getPhoto()));
                return photo;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });*/
    }

    public Department getDepartmentByName(String t) {
        return departmentRepository.findByName(t);
    }

    public Position getPositionByName(String t) {
        return positionRepository.findByName(t);
    }

    public void deletePerson(Person person) {
        personRepository.delete(person);
    }
}
