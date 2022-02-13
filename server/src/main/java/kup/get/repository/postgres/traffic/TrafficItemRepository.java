package kup.get.repository.postgres.traffic;

import kup.get.entity.postgres.traffic.TrafficItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TrafficItemRepository extends JpaRepository<TrafficItem, Long>{
    List<TrafficItem> findAllByName(String name);
    List<TrafficItem> findAllByDateFinishAfter(LocalDate date);
}