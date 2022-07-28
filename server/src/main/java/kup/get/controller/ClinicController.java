package kup.get.controller;

import kup.get.entity.alfa.ClinicEvent;
import kup.get.entity.postgres.traffic.TrafficPerson;
import kup.get.service.alfa.AlfaService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class ClinicController {
    private final AlfaService alfaService;

    @GetMapping("/clinic/event/{date}")
    Flux<ClinicEvent> getPeopleByTeam(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Flux.fromIterable(alfaService.getEventsByDate(date))
                .concatWith(Flux.fromIterable(alfaService.getVacationsByDate(date)))
                .concatWith(Flux.fromIterable(alfaService.getSickLeaveByDate(date)));
    }
}
