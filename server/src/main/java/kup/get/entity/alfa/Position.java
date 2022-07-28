package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sp_doljn")
@Getter
@Setter
@NoArgsConstructor
public class Position {
    @Id
    @Column(name = "KOD")
    private long id;
    @Column(name = "naim")
    private String name;
}