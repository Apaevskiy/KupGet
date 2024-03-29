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
    @Id
    @Column(name = "KOD")
    private long id;
    @Column(name = "NAIM")
    private String name;
}