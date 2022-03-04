package kup.get.entity.postgres.traffic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(name = "traffic_item_type")
@Getter
@Setter
@ToString
public class TrafficItemType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int defaultDurationInMonth;
    private boolean status;
    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "type")
    private TrafficItem item;
}
