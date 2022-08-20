package kup.get.entity.alfa.traffic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sp_avtomob")
@Getter
@Setter
@ToString
public class TrafficVehicle {
    @Id
    @Column(name = "KOD")
    private Long id;

    @Column(name = "gar_nom")
    private String number;
    @Column(name = "naim")
    private String model;

    @OneToMany(mappedBy = "vehicle")
    private List<TrafficTeam> teams;

}
