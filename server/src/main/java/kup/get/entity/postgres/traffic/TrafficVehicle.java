package kup.get.entity.postgres.traffic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="traffic_vehicle")
@Getter
@Setter
@ToString
public class TrafficVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int number;
    private String model;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "vehicle")
    private List<TrafficItem> items;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;
}
