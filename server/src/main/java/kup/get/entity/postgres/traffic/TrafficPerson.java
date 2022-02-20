package kup.get.entity.postgres.traffic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
public class TrafficPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long personnelNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "person")
    private List<TrafficItem> items;
}
