package kup.get.repository.postgres.energy;

import kup.get.entity.postgres.energy.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {
    List<Log> findAllByDateIsBetween(LocalDate dateStart, LocalDate dateFinish);

}
