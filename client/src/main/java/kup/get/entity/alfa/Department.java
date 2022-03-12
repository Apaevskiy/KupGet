package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "person_department")
@Getter
@Setter
@NoArgsConstructor
public class Department {
    private long id;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long number;
    private String name;
}
