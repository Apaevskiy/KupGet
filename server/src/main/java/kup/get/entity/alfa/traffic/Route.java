package kup.get.entity.alfa.traffic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SP_OT_MARSHRUT")
@Getter
@Setter
@ToString
public class Route {
    @Id
    @Column(name = "ku")
    private Long id;

    @Column(name = "naim")
    private String name;

}
