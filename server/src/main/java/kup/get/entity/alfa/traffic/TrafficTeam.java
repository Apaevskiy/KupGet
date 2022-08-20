package kup.get.entity.alfa.traffic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sp_ot_brig")
@Getter
@Setter
public class TrafficTeam {
    @Id
    @Column(name = "kod")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "k_tts")
    @NotFound(action = NotFoundAction.IGNORE)
    private TrafficVehicle vehicle;

    @OneToMany(mappedBy = "team")
    private List<ConsolidationTeam> consolidationTeams;

    @Override
    public String toString() {
        return "TrafficTeam{" +
                "id=" + id +
//                ", vehicle=" + vehicle +
                ", consolidationTeams=" + consolidationTeams +
                '}';
    }
}
