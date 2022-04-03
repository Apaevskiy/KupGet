package kup.get.repository.postgres.traffic;

import kup.get.entity.postgres.traffic.TrafficTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficTeamRepository  extends JpaRepository<TrafficTeam, Long> {
}