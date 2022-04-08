package kup.get.entity.traffic;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "traffic_item_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TrafficItemType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private int defaultDurationInMonth;
    private boolean status;
    @Column(unique = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
