package kup.get.entity.traffic;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "traffic_vehicle")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrafficVehicle  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private int number;
    private String model;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;
}
