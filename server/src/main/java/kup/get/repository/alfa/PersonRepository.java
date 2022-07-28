package kup.get.repository.alfa;

import kup.get.entity.alfa.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long>{
    @Query(value = "select * from sp_ol u where (u.D_UV>?1 or u.D_UV is null) and u.D_PR_NA_R is not null and K_PODR is not null and K_PODR!=0", nativeQuery = true)
    List<Person> findAllWorkPeople(String date);
}
