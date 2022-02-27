package kup.get.repository.postgres.traffic;

import kup.get.entity.postgres.traffic.TrafficItem;
import kup.get.entity.postgres.traffic.TrafficPerson;
import kup.get.entity.postgres.traffic.TrafficTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrafficPersonRepository extends JpaRepository<TrafficPerson, Long> {
    List<TrafficPerson> findAllByItemsTypeIdAndItemsDateFinishAfter(Long id, LocalDate date);
    List<TrafficPerson> findAllByTeamId(Long id);
}