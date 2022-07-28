package kup.get.repository.alfa;

import kup.get.entity.alfa.ClinicEvent;
import kup.get.entity.alfa.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ClinicRepository extends JpaRepository<ClinicEvent, Long>{
    List<ClinicEvent> findAllByDateClinicInspection(LocalDate date);

    @Query(value = "select KOD_IL as ku_ol from sp_ol_otp where d_n <= ?1 and d_o>=?1 group by kod_il",nativeQuery = true)
    List<Long> findVacation(LocalDate date);

    @Query(value = "select KOD from sp_z_bl where d_n <= ?1 and d_o>=?1 group by KOD",nativeQuery = true)
    List<Long> findSickLeave(LocalDate date);
}
