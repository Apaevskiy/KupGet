package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.postgres.security.Role;
import kup.get.entity.postgres.security.User;
import kup.get.service.alfa.AlfaService;
import kup.get.service.security.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class AsuController {

    private final AlfaService alfaService;
    private final UserService userService;

    @GetMapping("/asu/getRoles")
    Flux<Role> getRoles() {
        return Flux.fromIterable(userService.allRoles());
    }

    @GetMapping("/asu/getUsers")
    Flux<User> getUsers() {
        return Flux.fromIterable(userService.allUsers());
    }

    @PostMapping("/asu/savePeople")
    Flux<Person> savePeople(@RequestBody Flux<Person> people) {
        return people.map(alfaService::savePerson);
    }

    @DeleteMapping("/asu/deleteUser/{id}")
    Mono<Void> deleteUser(@PathVariable("id") long id) {
        return userService.deleteUser(id);
    }

    @PatchMapping("/asu/saveUser")
    Mono<User> saveUser(@RequestBody User user) {
        return Mono.just(userService.saveUser(user));
    }
}
