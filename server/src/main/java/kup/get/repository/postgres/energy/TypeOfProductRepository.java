package kup.get.repository.postgres.energy;

import kup.get.entity.postgres.energy.TypeOfProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfProductRepository extends JpaRepository<TypeOfProduct, Long> {
    TypeOfProduct getFirstById(Long id);
}