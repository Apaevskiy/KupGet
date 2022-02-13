package kup.get.service.alfa;

import kup.get.entity.alfa.Person;
import kup.get.repository.alfa.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AlfaService {
    private final PersonRepository personRepository;

    public List<Person> getDrivers(){
        return personRepository.findAllDriversByDepartmentNumber(String.valueOf(LocalDate.now()), 18L);
    }
}
