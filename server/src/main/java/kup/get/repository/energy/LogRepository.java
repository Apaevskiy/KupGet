package kup.get.repository.energy;

import kup.get.entity.energy.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;

public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {
    static Specification<Log> searchForLogs(LocalDate begin, LocalDate end) {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (begin != null) {
                Predicate _predicate = cb.greaterThanOrEqualTo(root.get("date"), begin);
                predicate = cb.and(predicate, _predicate);
            }
            LocalDate date;
            if (end == null) date = LocalDate.now();
            else date = end;
            Predicate _predicate = cb.lessThan(root.get("date"), date.plusDays(1));
            predicate = cb.and(predicate, _predicate);
            return predicate;
        };
    }
    static PageRequest getPageable() {
        return PageRequest.of(0, 100);
    }
}
