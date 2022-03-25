package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.postgres.security.Role;
import kup.get.entity.postgres.security.User;
import kup.get.service.Services;
import kup.get.service.alfa.AlfaService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Controller
@AllArgsConstructor
public class AsuController {

    private final Services services;
    private final AlfaService alfaService;


    @MessageMapping("asu.getRoles")
    Flux<Role> getRoles() {
        return Flux.fromIterable(services.getUserService().allRoles());
    }

    @MessageMapping("asu.getUsers")
    Flux<User> getUsers() {
        return Flux.fromIterable(services.getUserService().allUsers());
    }

    @MessageMapping("asu.savePeople")
    Flux<Person> savePeople(Flux<Person> people) {
        return people.map(alfaService::savePerson);
    }

    @MessageMapping("asu.deleteUser")
    Mono<Boolean> deleteUser(Mono<User> userMono) {
        return userMono.map(services.getUserService()::deleteUser);
    }

    @MessageMapping("asu.saveUser")
    Mono<User> saveUser(Mono<User> userMono) {
        return userMono.map(services.getUserService()::saveUser);
    }

    @MessageMapping("asu.update")
    Flux<HttpStatus> update(@Headers Map<String, Object> metadata, @Payload Flux<DataBuffer> content) throws IOException {
        for (String o : metadata.keySet())
            System.out.println("key: " + o + " value: " + metadata.get(o));
        System.out.println(metadata.get("file-extn"));
        return Flux.empty()/*Flux.concat(services.getVersionService().uploadFile(content), Mono.just(HttpStatus.ACCEPTED))
                .onErrorReturn(HttpStatus.CONFLICT)*/;
    }
}
