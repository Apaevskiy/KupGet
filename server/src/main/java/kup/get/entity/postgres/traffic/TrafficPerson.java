package kup.get.entity.postgres.traffic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "traffic_person")
@Getter
@Setter
public class TrafficPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "traffic_item_id"/*, nullable = false*/)
    private List<TrafficItem> items;
}
