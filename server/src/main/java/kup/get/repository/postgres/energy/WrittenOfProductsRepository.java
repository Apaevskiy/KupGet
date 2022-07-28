package kup.get.repository.postgres.energy;

import kup.get.entity.postgres.energy.WrittenOfProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface WrittenOfProductsRepository extends JpaRepository<WrittenOfProducts, Long> {

}
