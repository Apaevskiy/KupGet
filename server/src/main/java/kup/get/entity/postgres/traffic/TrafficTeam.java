package kup.get.entity.postgres.traffic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="traffic_team")
@Getter
@Setter
public class TrafficTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String number;
    private String workingMode;

    @OneToOne(mappedBy = "team", optional = false)
    private TrafficVehicle vehicle;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private Set<TrafficPerson> trafficPeople;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private Set<TrafficItem> trafficItems;
}
