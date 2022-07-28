package kup.get.repository.alfa;

import kup.get.entity.alfa.AlfaUser;
import kup.get.entity.alfa.ClinicEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlfaUserRepository extends JpaRepository<AlfaUser, Long>{
}
