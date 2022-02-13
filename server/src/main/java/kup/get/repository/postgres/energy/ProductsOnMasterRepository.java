package kup.get.repository.postgres.energy;

import kup.get.entity.postgres.energy.ProductsOnMaster;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.criteria.Predicate;
import java.util.Locale;

public interface ProductsOnMasterRepository extends JpaRepository<ProductsOnMaster, Long>,
        JpaSpecificationExecutor<ProductsOnMaster> {
    ProductsOnMaster getFirstById(Long id);

    static Specification<ProductsOnMaster> searchForWaybills(Long waybillsId,
                                                             Long numberRecord,
                                                             String name) {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if(waybillsId != null) {
                Predicate _predicate = cb.equal(root.get("product").get("waybills").get("id"), waybillsId);
                predicate = cb.and(predicate,_predicate);
            }
            if(numberRecord != null) {
                Predicate _predicate = cb.equal(root.get("type").get("id"), numberRecord);
                predicate = cb.and(predicate,_predicate);
            }
            if(name != null && !name.isEmpty()) {
                String nameToLowerCase = name.toLowerCase(Locale.ENGLISH);
                Predicate _predicate = cb.like(cb.lower(root.get("product").get("type").get("name")), "%"+nameToLowerCase+"%");
                predicate = cb.and(predicate,_predicate);
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            predicate = cb.and(predicate, cb.equal(root.get("user").get("username"), auth.getName()));
            predicate = cb.and(predicate, cb.greaterThan(root.get("remaining"),0));

            return predicate;
        };
    }
    static PageRequest getPageable(){
        return PageRequest.of(0,100);
    }
}
