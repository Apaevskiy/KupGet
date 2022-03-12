package kup.get.repository.traffic;

import kup.get.entity.traffic.TrafficVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficVehicleRepository extends JpaRepository<TrafficVehicle, Long> {
}