package kup.get.entity.alfa.traffic;

import kup.get.entity.alfa.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sp_ot_brig_ol")
@NoArgsConstructor
@Getter
@Setter
public class ConsolidationTeam {

    @Id
    @Column(name = "kod")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kod_il")
    private TrafficTeam team;

    @Column(name = "d_n")
    private LocalDate dateStart;
    @Column(name = "d_o")
    private LocalDate dateFinish;

    /*@ManyToOne
    @JoinColumn(name = "k_ts")
    private TrafficVehicle vehicle;*/

    @ManyToOne
    @JoinColumn(name = "ku_ol")
    private Person person;

    @Override
    public String toString() {
        return "ConsolidationTeam{" +
                "id=" + id +
//                ", team=" + team +
                ", dateStart=" + dateStart +
                ", dateFinish=" + dateFinish +
                ", person=" + person +
                '}';
    }
}
