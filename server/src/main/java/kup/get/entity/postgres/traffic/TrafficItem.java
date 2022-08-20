package kup.get.entity.postgres.traffic;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traffic_item")
@Getter
@Setter
@ToString
public class TrafficItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateStart;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFinish;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "item_type_id")
    private TrafficItemType type;

    private Long teamId;
    private Long personId;
    private Long vehicleId;
}
