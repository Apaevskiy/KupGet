package kup.get.entity.traffic;

import kup.get.entity.alfa.Person;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traffic_item")
@Getter
@Setter
@ToString
public class TrafficItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private String description;

    @DateTimeFormat(pattern="dd.MM.yyyy")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private LocalDate dateFinish;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_type_id")
    private TrafficItemType type;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private TrafficTeam team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private TrafficVehicle vehicle;

    @Transient
    private transient Long personId;
    @Transient
    private transient Long teamId;
    @Transient
    private transient Long vehicleId;
}
