package kup.get.service;

import kup.get.entity.energy.ProductsOnMaster;
import kup.get.repository.energy.ProductsOnMasterRepository;
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
