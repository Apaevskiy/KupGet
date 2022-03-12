package kup.get.entity.traffic;

import kup.get.entity.alfa.Person;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
@NoArgsConstructor
public class TrafficPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long personnelNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;


    public TrafficPerson(Person person) {
        personnelNumber = person.getId();
    }
}
