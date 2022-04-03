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

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFinish;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_type_id")
    private TrafficItemType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private TrafficTeam team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @Transient
    private TrafficPerson trafficPerson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private TrafficVehicle vehicle;
}
