package kup.get.controller;

import kup.get.entity.alfa.Person;
import kup.get.entity.postgres.traffic.TrafficItemType;
import kup.get.entity.postgres.traffic.TrafficPerson;
import kup.get.service.alfa.AlfaService;
import kup.get.service.traffic.TrafficItemService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
public class BriefingController {
    private final TrafficItemService trafficItemService;
    private final AlfaService alfaService;

    @MessageMapping("briefing")
    public Flux<TrafficPerson> getBriefing(Mono<LocalDate> dateMono) {
        return dateMono
                .flatMapMany(date -> Flux.fromIterable(trafficItemService.getBriefingOfPeople(date)));
    }

    @MessageMapping("drivers")
    public Flux<Person> getDrivers() {
        return Flux.fromIterable(alfaService.getDrivers());
    }

    @MessageMapping("greetings")
    Flux<String> greet(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user
                .flatMapMany(u ->
                        Flux.fromIterable(u.getAuthorities()
                                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
    }

//    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    @MessageMapping("traffic.trafficItemType")
    public Flux<TrafficItemType> getTrafficItemType() {
        System.out.println("HI");
        return Flux.fromIterable(trafficItemService.getAllItemTypes());
    }

    @MessageMapping("saveItemsTypes")
    public Mono<Boolean> saveItemsTypes(Flux<TrafficItemType> flux) {
        return flux
                .collect(Collectors.toList())
                .doOnNext(trafficItemService::saveType)
                .map(e -> true)
                .onErrorReturn(false);
    }
}
