package kup.get.repository.energy;

import kup.get.entity.energy.Waybills;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.Locale;

public interface WaybillsRepository extends JpaRepository<Waybills, Long>, JpaSpecificationExecutor<Waybills> {
    boolean existsById(Long id);


    static Specification<Waybills> search(Long id,
                                          String person,
                                          String department,
                                          LocalDate begin,
                                          LocalDate end) {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (id != null) {
                Predicate _predicate = cb.equal(root.get("id"), id);
                predicate = cb.and(predicate, _predicate);
            }
            if (person != null && !person.isEmpty()) {
                String personToLowerCase = person.toLowerCase(Locale.ENGLISH);
                Predicate _predicate = cb.like(cb.lower(root.get("person")), "%" + personToLowerCase + "%");
                predicate = cb.and(predicate, _predicate);
            }
            if (department != null && !department.isEmpty()) {
                String depToLowerCase = department.toLowerCase(Locale.ENGLISH);
                Predicate _predicate = cb.like(cb.lower(root.get("department")), "%" + depToLowerCase + "%");
                predicate = cb.and(predicate, _predicate);
            }
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