package kup.get.repository.energy;

import kup.get.entity.energy.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Locale;


public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Product findOneById(Long id);
    List<Product> findAllByWaybillsId(Long id);
    static Specification<Product> searchForWaybills(Long waybillsId,
                                                    Long numberRecord,
                                                    String name) {
        return (root, cq, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if(waybillsId != null) {
                Predicate _predicate = cb.equal(root.get("waybills").get("id"), waybillsId);
                predicate = cb.and(predicate,_predicate);
            }
            if(numberRecord != null) {
                Predicate _predicate = cb.equal(root.get("type").get("id"), numberRecord);
                predicate = cb.and(predicate,_predicate);
            }
            if(name != null && !name.isEmpty()) {
                String nameToLowerCase = name.toLowerCase(Locale.ENGLISH);
                Predicate _predicate = cb.like(cb.lower(root.get("type").get("name")), "%"+nameToLowerCase+"%");
                predicate = cb.and(predicate,_predicate);
            }
            predicate = cb.and(predicate, cb.greaterThan(root.get("remaining"),0));
            return predicate;
        };
    }
    static PageRequest getPageable(){
        return PageRequest.of(0,100);
    }
}