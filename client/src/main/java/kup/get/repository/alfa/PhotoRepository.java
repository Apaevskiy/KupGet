package kup.get.repository.alfa;

import kup.get.entity.alfa.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.sql.rowset.serial.SerialBlob;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Photo findFirstByPersonId(Long id);

    @Modifying
    @Query(value = "insert into person_photo (id, person_id, photo) values (?1, ?2, ?3)", nativeQuery = true)
    void savePhoto(Long id, Long personId, SerialBlob blob);
}
