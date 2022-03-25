package kup.get.service.socket;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.entity.security.Role;
import kup.get.entity.security.User;
import kup.get.service.PersonService;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    public Mono<byte[]> getPhotoByPerson(Long id) {
        return socketService.route("traffic.getPhotoByPerson")
                .data(id)
                .retrieveMono(byte[].class);
    }

    public Flux<Person> savePeople(List<Person> people) {
        return socketService.route("asu.savePeople")
                .data(Flux.fromIterable(people))
                .retrieveFlux(Person.class);
    }

    public Flux<Photo> addPhotoToPeople() {
        return socketService.route("traffic.getPhotoByPeople")
                .retrieveFlux(Photo.class);
    }

    public Flux<Role> getRoles() {
        return socketService.route("asu.getRoles")
                .retrieveFlux(Role.class);
    }

    public Flux<User> getUsers() {
        return socketService.route("asu.getUsers")
                .retrieveFlux(User.class);
    }

    public Mono<Boolean> deleteUser(User user) {
        return socketService.route("asu.deleteUser")
                .data(user)
                .retrieveMono(Boolean.class);
    }

    public Mono<User> saveUser(User user) {
        return socketService.route("asu.saveUser")
                .data(user)
                .retrieveMono(User.class);
    }
}
