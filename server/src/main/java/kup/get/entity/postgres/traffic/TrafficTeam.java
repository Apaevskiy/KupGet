package kup.get.entity.postgres.traffic;

import lombok.Data;
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

    private String number;

    @OneToMany
    @JoinColumn(name = "traffic_people_id")
    private List<TrafficPerson> trafficPeople;

    @OneToMany
    @JoinColumn(name = "traffic_item_id")
    private List<TrafficItem> trafficItems;
}
