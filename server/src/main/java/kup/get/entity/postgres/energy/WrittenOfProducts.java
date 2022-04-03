package kup.get.entity.postgres.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "written_of")
public class WrittenOfProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double numberRequire;
    private double remaining;
    private String comment;
    private LocalDate dateWrittenOf;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "written_to_master_id", nullable = false)
    private ProductsOnMaster productsOnMaster;
}
