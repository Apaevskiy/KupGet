package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sp_podr")
@Getter
@Setter
@NoArgsConstructor
public class Department {
    @Column(name = "KU")
    private long id;
    @Id
    @Column(name = "KOD")
    private long number;
    @Column(name = "NAIM")
    private String name;
}
