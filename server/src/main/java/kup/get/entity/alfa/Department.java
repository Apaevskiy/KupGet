package kup.get.entity.alfa;

import lombok.*;

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
    private long ku;
    private String naim;
}
