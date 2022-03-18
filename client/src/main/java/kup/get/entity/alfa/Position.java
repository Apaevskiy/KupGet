package kup.get.entity.alfa;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_position")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Position  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private long id;
    private String name;
}
