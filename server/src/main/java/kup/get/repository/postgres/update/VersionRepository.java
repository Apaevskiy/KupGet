package kup.get.repository.postgres.update;

import kup.get.entity.postgres.update.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VersionRepository extends JpaRepository<Version, Long> {
    Version findFirstByRelease(String release);
    List<Version> findAllByIdAfter(Long id);
    Version findFirstByOrderByIdDesc();
}
