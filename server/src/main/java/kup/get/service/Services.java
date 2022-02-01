package kup.get.service;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Services {
    private final UserService userService;
    private final TypeOfProductService typeOfProductService;
    private final WaybillsService waybillsService;
    private final ProductService productService;
    private final ProductsOnMasterService masterService;
    private final WrittenOfProductsService writtenOfService;
    private final VersionService versionService;

    public Services(UserService userService, TypeOfProductService typeOfProductService, WaybillsService waybillsService, ProductService productService, ProductsOnMasterService masterService, WrittenOfProductsService writtenOfService, VersionService versionService) {
        this.userService = userService;
        this.typeOfProductService = typeOfProductService;
        this.waybillsService = waybillsService;
        this.productService = productService;
        this.masterService = masterService;
        this.writtenOfService = writtenOfService;
        this.versionService = versionService;
    }
}
