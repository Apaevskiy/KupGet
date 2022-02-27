package kup.get.entity.postgres.traffic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "traffic_vehicle", catalog = "traffic")
@Getter
@Setter
@ToString
public class TrafficVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String number;
    private String model;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "vehicle")
    private List<TrafficItem> items;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "traffic_team_id", nullable = false)
    private TrafficTeam team;
}
