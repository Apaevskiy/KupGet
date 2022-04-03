package kup.get.entity.postgres.traffic;

import kup.get.entity.alfa.Person;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
public class TrafficPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;
}
