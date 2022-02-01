package kup.get.service;

import kup.get.entity.energy.TypeOfProduct;
import kup.get.repository.energy.TypeOfProductRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypeOfProductService {
    private final TypeOfProductRepository repository;
    private final LogService logService;
    public TypeOfProductService(TypeOfProductRepository repository, LogService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    public TypeOfProduct getProductOfRecord(Long record) {
        return repository.getFirstById(record);
    }

    @Transactional
    public void addAllTypeOfProduct(List<TypeOfProduct> types){
        logService.addLog("Добавление товаров " + types.stream().map(TypeOfProduct::getId).collect(Collectors.toList()));
        repository.saveAll(types);
    }
    @Transactional
    public void addTypeOfProduct(TypeOfProduct types){
        logService.addLog("Добавление товара " + types.getId());
        repository.save(types);
    }
}
