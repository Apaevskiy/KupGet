package kup.get.entity.alfa;

import lombok.*;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "k_podr")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "k_doljn")
    private Position position;

    @Column(name = "FAM")
    private String lastName;

    @Column(name = "IM")
    private String firstName;

    @Column(name = "OTCH")
    private String middleName;
}
