package kup.get.repository.alfa;

import kup.get.entity.alfa.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Photo findFirstByPersonId(Long id);
}
