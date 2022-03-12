package kup.get.entity.traffic;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "traffic_item_type")
@Getter
@Setter
public class TrafficItemType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int defaultDurationInMonth;
    private boolean status;
    private boolean changed = false;
    @Column(unique = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
