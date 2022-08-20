package kup.get.repository.alfa;

import kup.get.entity.alfa.traffic.TrafficTeam;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;

public interface TrafficTeamRepository extends JpaRepository<TrafficTeam, Long> {

    /*@Query(value = "select br.kod, m.ku as route_ku, m.naim as route_naim from sp_ot_brig br " +
            "left join sp_ot_area ar on ar.kod=br.KOD_IL " +
            "left join SP_OT_MARSHRUT m on ar.KD=m.KU", nativeQuery = true)
    List<TrafficTeam> findAllTeams();
*/
    @Override
    @NonNull
    @EntityGraph(attributePaths = {/*"vehicle", */"consolidationTeams", "consolidationTeams.person", "consolidationTeams.person.department", "consolidationTeams.person.position"})
    List<TrafficTeam> findAll();
}