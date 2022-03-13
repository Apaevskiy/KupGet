package kup.get.entity.traffic;

import com.sun.xml.internal.ws.developer.Serialization;
import kup.get.entity.alfa.Person;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
@NoArgsConstructor
public class TrafficPerson implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long personnelNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;

    public TrafficPerson(Long id, Long personnelNumber) {
        this.id = id;
        this.personnelNumber = personnelNumber;
    }

    public TrafficPerson(Person person) {
        personnelNumber = person.getId();
    }
}
