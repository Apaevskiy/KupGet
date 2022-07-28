package kup.get.service.socket;

import kup.get.entity.alfa.Person;
import kup.get.entity.alfa.Photo;
import kup.get.entity.security.Role;
import kup.get.entity.security.User;
import kup.get.service.PersonService;
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

    }

    public Flux<Person> getPeople() {
        return socketService.getClient().get().uri("/auth/people")
                .retrieve().bodyToFlux(Person.class);
    }

    public Mono<Person> savePerson(Person person) {
        return socketService.getClient().post().uri("/asu/savePerson")
                .bodyValue(person)
                .retrieve().bodyToMono(Person.class);
    }

    @Deprecated
    public Mono<byte[]> getPhotoByPerson(Long id) {
        return socketService.getClient().get().uri("/auth/getPhotoByPerson/{id}",id)
                .retrieve().bodyToMono(byte[].class);
    }

    @Deprecated
    public Flux<Person> savePeople(List<Person> people) {
        return socketService.getClient().post().uri("/asu/savePeople")
                .bodyValue(Flux.fromIterable(people))
                .retrieve().bodyToFlux(Person.class);
    }

    @Deprecated
    public Flux<Photo> addPhotoToPeople() {
        return socketService.getClient().get().uri("/auth/getPhotoByPeople")
                .retrieve().bodyToFlux(Photo.class);
    }

    @Deprecated
    @Override
    public Flux<Photo> savePhotos(List<Photo> photos) {
        return null;
    }

    public Flux<Role> getRoles() {
        return socketService.getClient().get().uri("/asu/getRoles")
                .retrieve().bodyToFlux(Role.class);
    }

    public Flux<User> getUsers() {
        return socketService.getClient().get().uri("/asu/getUsers")
                .retrieve().bodyToFlux(User.class);
    }

    @Deprecated
    public Mono<Boolean> deleteUser(User user) {
        return socketService.getClient().delete().uri("/asu/deleteUser/{id}",user.getId())
                .retrieve().bodyToMono(Boolean.class);
    }

    public Mono<User> saveUser(User user) {
        return socketService.getClient().patch().uri("/asu/saveUser")
                .bodyValue(user)
                .retrieve().bodyToMono(User.class);
    }
}
