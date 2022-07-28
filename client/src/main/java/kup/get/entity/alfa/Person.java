package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private String personnelNumber;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "department", nullable = true)
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "position", nullable = true)
    private Position position;

    private String lastName;
    private String firstName;
    private String middleName;

    private Integer rank;

    @Transient
    transient private byte[] photo;

    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }

    /*@Override
    public String toString() {
        return personnelNumber + ' ' + lastName + ' ' + firstName + ' ' + middleName;
    }*/
}
