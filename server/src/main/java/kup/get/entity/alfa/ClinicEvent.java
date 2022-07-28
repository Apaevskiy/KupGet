package kup.get.entity.alfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "SP_PMU_MED_OSMOTR")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClinicEvent {
    @Id
    @Column(name = "KU")
    private long id;

    @Column(name = "d_vvod")
    private LocalDate dateClinicInspection;

    @Column(name = "t_vvod")
    private Time time;

    private int pnp;

//    @ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "ku_ol")
    private Long personId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uku")
    private AlfaUser user;

    //    private Date dateCheckpointInspection;
}
