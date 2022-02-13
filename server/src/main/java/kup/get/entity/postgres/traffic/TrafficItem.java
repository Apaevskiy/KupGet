package kup.get.entity.postgres.traffic;

import kup.get.entity.postgres.energy.TypeOfProduct;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traffic_item")
@Getter
@Setter
public class TrafficItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_item_type_id")
    private TrafficItemType type;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFinish;
}
