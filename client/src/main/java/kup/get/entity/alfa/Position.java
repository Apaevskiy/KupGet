package kup.get.entity.alfa;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "person_position")
@Getter
@Setter
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long number;
    private String name;
}
