package kup.get.entity.traffic;

import com.sun.xml.internal.ws.developer.Serialization;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "traffic_item_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrafficItemType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long localId;

    private Long id;
    private int defaultDurationInMonth;
    private boolean status;
    private boolean changed = false;
    @Column(unique = true)
    private String name;

    public TrafficItemType(Long id, int defaultDurationInMonth, String name) {
        this.id = id;
        this.defaultDurationInMonth = defaultDurationInMonth;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
