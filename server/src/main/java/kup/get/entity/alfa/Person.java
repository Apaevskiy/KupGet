package kup.get.entity.alfa;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


@Entity
@Table(name = "sp_ol")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Person {
    @Id
    @Column(name = "KU")
    private long id;

    @Column(name = "TAB_NOM")
    private String personnelNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "k_podr")
    @NotFound(action = NotFoundAction.IGNORE)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "k_doljn")
    @NotFound(action = NotFoundAction.IGNORE)
    private Position position;

    @Column(name = "FAM")
    private String lastName;

    @Column(name = "IM")
    private String firstName;

    @Column(name = "OTCH")
    private String middleName;

    @Column(name ="RAZR")
    private Integer rank;
}