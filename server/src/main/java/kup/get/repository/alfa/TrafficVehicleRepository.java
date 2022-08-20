package kup.get.repository.alfa;

import kup.get.entity.alfa.traffic.TrafficVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrafficVehicleRepository extends JpaRepository<TrafficVehicle, Long> {
    @Query(value = "select kod, naim, gar_nom from sp_avtomob where gar_nom is not null and gar_nom <> ''", nativeQuery = true)
    List<TrafficVehicle> findAllByNumberIsNotEmpty();
}