package kup.get.service;

import kup.get.service.energy.*;
import kup.get.service.security.UserService;
import kup.get.service.update.VersionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
@AllArgsConstructor
public class Services {
    private final UserService userService;
    private final TypeOfProductService typeOfProductService;
    private final WaybillsService waybillsService;
    private final ProductService productService;
    private final ProductsOnMasterService masterService;
    private final WrittenOfProductsService writtenOfService;
    private final VersionService versionService;
}
