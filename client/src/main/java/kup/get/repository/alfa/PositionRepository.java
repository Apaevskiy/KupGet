package kup.get.repository.alfa;

import kup.get.entity.alfa.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Position findByName(String t);

    Position findFirstById(long id);
}