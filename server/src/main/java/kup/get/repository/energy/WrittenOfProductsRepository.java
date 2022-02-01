package kup.get.repository.energy;

import kup.get.entity.energy.WrittenOfProducts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;

public interface WrittenOfProductsRepository extends JpaRepository<WrittenOfProducts, Long>, JpaSpecificationExecutor<WrittenOfProducts> {

    static Specification<WrittenOfProducts> userFilter() {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            predicate = cb.and(predicate, cb.equal(root.get("productsOnMaster").get("user").get("username"), auth.getName()));
            return predicate;
        };
    }

    static Specification<WrittenOfProducts> dataFilter(LocalDate begin, LocalDate end) {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (begin != null) {
                Predicate _predicate = cb.greaterThanOrEqualTo(root.get("dateWrittenOf"), begin);
                predicate = cb.and(predicate, _predicate);
            }
            LocalDate date;
            if (end == null) date = LocalDate.now();
            else date = end;
            Predicate _predicate = cb.lessThan(root.get("dateWrittenOf"), date.plusDays(1));
            predicate = cb.and(predicate, _predicate);
            return predicate;
        };
    }

    static PageRequest getPageable() {
        return PageRequest.of(0, 100);
    }
}
