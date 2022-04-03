package kup.get.entity.postgres.energy;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String status;
    private double numberRequire;
    private double numberReleased;
    private double price;
    private double remaining;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_of_product_id", nullable = false)
    private TypeOfProduct type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waybills_id", nullable = false)
    private Waybills waybills;
}


