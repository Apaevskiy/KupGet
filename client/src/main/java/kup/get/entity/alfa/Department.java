package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Department  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private long id;
    private String name;
}
