package kup.get.entity.traffic;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="traffic_team")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrafficTeam  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private String number;
    private String workingMode;
}
