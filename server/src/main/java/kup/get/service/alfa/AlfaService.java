package kup.get.service.alfa;

import kup.get.entity.alfa.ClinicEvent;
import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.repository.alfa.ClinicRepository;
import kup.get.repository.alfa.PersonRepository;
import kup.get.repository.alfa.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AlfaService {
    private final PersonRepository personRepository;
    private final PhotoRepository photoRepository;
    private final ClinicRepository clinicRepository;

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

    public List<ClinicEvent> getEventsByDate(LocalDate date){
        return clinicRepository.findAllByDateClinicInspection(date);
    }
    public List<ClinicEvent> getVacationsByDate(LocalDate date){
        return clinicRepository.findVacation(date).stream().map(l -> {
            ClinicEvent clinicEvent = new ClinicEvent();
            clinicEvent.setPersonId(l);
            clinicEvent.setPnp(-2);
            return clinicEvent;
        }).collect(Collectors.toList());
    }
    public List<ClinicEvent> getSickLeaveByDate(LocalDate date){
        return clinicRepository.findSickLeave(date).stream().map(l -> {
            ClinicEvent clinicEvent = new ClinicEvent();
            clinicEvent.setPersonId(l);
            clinicEvent.setPnp(-1);
            return clinicEvent;
        }).collect(Collectors.toList());
    }
}