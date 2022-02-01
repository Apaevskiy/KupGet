package kup.get.entity.alfa;

import lombok.*;

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
    private long ku;
    private String naim;
}
