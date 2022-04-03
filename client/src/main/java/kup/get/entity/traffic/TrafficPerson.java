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
@ToString
public class TrafficPerson implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person transientPerson;

    @Transient
    private transient Long person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_team_id")
    private TrafficTeam team;

}
