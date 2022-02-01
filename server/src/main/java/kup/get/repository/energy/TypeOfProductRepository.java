package kup.get.repository.energy;

import kup.get.entity.energy.TypeOfProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfProductRepository extends JpaRepository<TypeOfProduct, Long> {
    TypeOfProduct getFirstById(Long id);
}