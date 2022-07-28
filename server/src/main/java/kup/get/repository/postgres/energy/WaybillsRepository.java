package kup.get.repository.postgres.energy;

import kup.get.entity.postgres.energy.Waybills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface WaybillsRepository extends JpaRepository<Waybills, Long> {
}