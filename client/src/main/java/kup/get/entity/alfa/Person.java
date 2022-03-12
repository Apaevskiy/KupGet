package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String personnelNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position")
    private Position position;

    private String lastName;
    private String firstName;
    private String middleName;
    private Integer rank;


    @Override
    public String toString() {
        return personnelNumber + ' ' + lastName + ' ' + firstName + ' ' + middleName;
    }
}
