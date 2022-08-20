package kup.get.entity.traffic;

import kup.get.entity.alfa.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traffic_consolidation_team")
@NoArgsConstructor
@Getter
@Setter
public class ConsolidationTeam {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private TrafficTeam team;

    private LocalDate dateStart;
    private LocalDate dateFinish;

    @ManyToOne
    @JoinColumn(name = "vehicleId")
    private TrafficVehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;
}
