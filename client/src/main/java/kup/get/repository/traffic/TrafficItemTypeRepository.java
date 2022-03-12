package kup.get.repository.traffic;

import kup.get.entity.traffic.TrafficItemType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficItemTypeRepository extends JpaRepository<TrafficItemType, Long> {
    TrafficItemType findFirstByName(String name);
    TrafficItemType findFirstById(Long id);
}