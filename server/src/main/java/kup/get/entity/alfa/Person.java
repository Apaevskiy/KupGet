package kup.get.entity.alfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "sp_ol")
@Getter
@Setter
@NoArgsConstructor
public class Person {
    @Id
    private long KU;
    private String TAB_NOM;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "k_podr", nullable = false)
    private Department K_PODR;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "k_doljn", nullable = false)
    private Position K_DOLJN;

    private String FAM;
    private String IM;
    private String OTCH;
}
