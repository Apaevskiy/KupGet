package kup.get.entity.postgres.energy;

import kup.get.entity.postgres.security.User;
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
@Table(name = "products_on_master")
public class ProductsOnMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double numberRequire;
    private double remaining;
    private LocalDate dateWrittenToMaster;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "t_user_id", nullable = false)
    private User user;
    public Long getNumberWaybills(){
        return product.getWaybills().getId();
    }
}
