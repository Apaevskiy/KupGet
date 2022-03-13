package kup.get.service.socket;

import kup.get.entity.alfa.Person;
import kup.get.service.PersonService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonSocketService implements PersonService {
    private final List<Person> people = new ArrayList<>();
    private final SocketService socketService;

    public PersonSocketService(SocketService socketService) {
        this.socketService = socketService;
    }

    public void updatePeople() {
            socketService.route("traffic.people")
                    .retrieveFlux(Person.class)
                    .subscribe(people::add);

//        if(p.getPosition()!=null && p.getPosition().getId()==8347)
    }

    public List<Person> getPeople() {
        return people;
    }

    public Mono<Person> savePerson(Person person) {     // DBA
        return socketService.route("asu.savePerson")
                .data(person)
                .retrieveMono(Person.class);
    }

    public byte[] getPhotoByPerson(Long id) {
        return socketService.route("getPhotoByPerson")
                .data(id)
                .retrieveMono(byte[].class)
                .block();
    }
}
