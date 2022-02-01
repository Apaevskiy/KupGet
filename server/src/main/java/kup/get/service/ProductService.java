package kup.get.service;

import kup.get.entity.energy.Product;
import kup.get.repository.energy.ProductRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final TypeOfProductService typeOfProductService;
    private final LogService logService;

    public ProductService(ProductRepository repository, TypeOfProductService typeOfProductService, LogService logService) {
        this.repository = repository;
        this.typeOfProductService = typeOfProductService;
        this.logService = logService;
    }

    public List<Product> searchOfProducts(Long waybillsId, Long numberRecord, String name) {
        return repository.findAll(ProductRepository.searchForWaybills(waybillsId, numberRecord, name),
                ProductRepository.getPageable()).getContent();
    }

    public Product findProductsById(Long id) {
        return repository.findOneById(id);
    }

    public List<Product> findAllByWaybillsId(Long id) {
        return repository.findAllByWaybillsId(id);
    }


    @Transactional
    public int deleteProduct(long id) {
        logService.addLog("Удаление товара с id " + id);
        Product product = repository.findOneById(id);
        if (product.getNumberReleased() - product.getRemaining() != 0) return -1;
        try {
            repository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateProduct(Product newProduct) {
        Product oldProduct = repository.findOneById(newProduct.getId());
        double remaining = (newProduct.getNumberReleased() - (oldProduct.getNumberReleased() - oldProduct.getRemaining()));
        if (remaining < 0) return -1;
        logService.addLog("Обновление товара " + oldProduct.getId());
        try {
            typeOfProductService.addAllTypeOfProduct(Collections.singletonList(newProduct.getType()));
            oldProduct.setType(newProduct.getType());
            oldProduct.setStatus(newProduct.getStatus());
            oldProduct.setNumberRequire(newProduct.getNumberRequire());
            oldProduct.setNumberReleased(newProduct.getNumberReleased());
            oldProduct.setPrice(newProduct.getPrice());
            oldProduct.setRemaining(remaining);
            oldProduct.setStatus(newProduct.getStatus());
            repository.save(oldProduct);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public boolean addProductToWaybill(Product product) {
        logService.addLog("Добавление товара в накладную " + product.getId());
        try {
            product.setRemaining((long) product.getNumberReleased());
            repository.saveAndFlush(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void addProducts(List<Product> products) {
        logService.addLog("Добавление товаров " + products.stream().map(Product::getId).collect(Collectors.toList()));
        repository.saveAllAndFlush(products);
    }

    @Transactional
    public void writeToMaster(Product product, Double count) {
        logService.addLog("Выписал товар " + product.getType().getName() + " в колическтве " + count + " " + product.getType().getUnit());
        repository.save(product);
    }
}
