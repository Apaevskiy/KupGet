package kup.get.repository.postgres.traffic;

import kup.get.entity.postgres.traffic.TrafficItem;
import kup.get.entity.postgres.traffic.TrafficItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrafficItemTypeRepository extends JpaRepository<TrafficItemType, Long> {
    TrafficItemType findFirstByName(String name);
    TrafficItemType findFirstById(Long id);
}