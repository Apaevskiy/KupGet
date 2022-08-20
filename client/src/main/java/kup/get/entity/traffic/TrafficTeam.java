package kup.get.entity.traffic;

import kup.get.entity.alfa.Person;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="traffic_team")
@Getter
@Setter
public class TrafficTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToMany
    private List<Person> people;
    @OneToOne
    @JoinColumn(name = "vehicleId")
    private TrafficVehicle vehicle;
}
