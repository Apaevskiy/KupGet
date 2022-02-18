package kup.get.repository.postgres.traffic;

import kup.get.entity.postgres.traffic.TrafficTeam;
import kup.get.entity.postgres.traffic.TrafficVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficVehicleRepository extends JpaRepository<TrafficVehicle, Long> {
}