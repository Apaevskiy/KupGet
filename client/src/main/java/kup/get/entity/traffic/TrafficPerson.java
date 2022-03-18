package kup.get.entity.traffic;

import kup.get.entity.alfa.Person;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrafficPerson implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private Long personId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;

    public TrafficPerson(Person person) {
        personId = person.getId();
    }
}
