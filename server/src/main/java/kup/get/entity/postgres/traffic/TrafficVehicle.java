package kup.get.entity.postgres.traffic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="traffic_vehicle")
@Getter
@Setter
public class TrafficVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int number;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_item_id")
    private List<TrafficItem> items;
}
