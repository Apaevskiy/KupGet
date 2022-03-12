package kup.get.service.socket;

import kup.get.config.RSocketClientBuilderImpl;
import kup.get.entity.alfa.Person;
import kup.get.service.PersonService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonSocketService extends SocketService implements PersonService {
    private final List<Person> people = new ArrayList<>();

    public PersonSocketService( RSocketClientBuilderImpl config) {
        super(config);
    }

    public void updatePeople() {
            route("traffic.people")
                    .retrieveFlux(Person.class)
                    .subscribe(people::add);

//        if(p.getPosition()!=null && p.getPosition().getId()==8347)
    }

    public List<Person> getPeople() {
        return people;
    }

    public Mono<Person> savePerson(Person person) {     // DBA
        return route("asu.savePerson")
                .data(person)
                .retrieveMono(Person.class);
    }

    public byte[] getPhotoByPerson(Long id) {
        return route("getPhotoByPerson")
                .data(id)
                .retrieveMono(byte[].class)
                .block();
    }
}
