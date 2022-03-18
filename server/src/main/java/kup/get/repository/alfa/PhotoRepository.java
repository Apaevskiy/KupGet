package kup.get.repository.alfa;

import kup.get.entity.alfa.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Photo findFirstByPersonId(Long id);

    @Query(value = "select * from SP_OL_BLOBS where KOD_IL in (select KU from sp_ol u where (u.D_UV>?1 or u.D_UV is null) and u.D_PR_NA_R is not null)", nativeQuery = true)
    List<Photo> findAllByIdIn(String date);
}
