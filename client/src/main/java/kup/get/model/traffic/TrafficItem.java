package kup.get.model.traffic;

import kup.get.model.alfa.Person;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class TrafficItem {
    private Long id;
    private String name;
    private String description;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFinish;

    private TrafficItemType type;

    private TrafficTeam team;
    private TrafficPerson person;
    private TrafficVehicle vehicle;
}
