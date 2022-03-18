package kup.get.repository.traffic;

import kup.get.entity.traffic.TrafficPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrafficPersonRepository extends JpaRepository<TrafficPerson, Long> {
//    List<TrafficPerson> findAllByItemsTypeIdAndItemsDateFinishAfter(Long id, LocalDate date);
    List<TrafficPerson> findAllByTeamId(Long id);
    TrafficPerson findFirstByPersonId(Long personnelNumber);
}