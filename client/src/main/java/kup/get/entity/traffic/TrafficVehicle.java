package kup.get.entity.traffic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "traffic_vehicle")
@Getter
@Setter
@ToString
public class TrafficVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String number;
    private String model;

    @OneToOne
    private TrafficTeam team;
}
