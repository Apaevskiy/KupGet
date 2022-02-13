package kup.get.service.energy;

import kup.get.entity.postgres.energy.ProductsOnMaster;
import kup.get.repository.postgres.energy.ProductsOnMasterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsOnMasterService {
    private final ProductsOnMasterRepository repository;
    public ProductsOnMasterService(ProductsOnMasterRepository repository) {
        this.repository = repository;
    }

    public void save(ProductsOnMaster product){
        repository.save(product);
    }
    public List<ProductsOnMaster> findAll(Long waybillsId, Long record, String name){
        return repository.findAll(
                ProductsOnMasterRepository.searchForWaybills(waybillsId, record, name),
                ProductsOnMasterRepository.getPageable()).getContent();
    }
    public  ProductsOnMaster findById(Long id){
        return repository.getFirstById(id);
    }
}
