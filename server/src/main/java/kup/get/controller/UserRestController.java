package kup.get.controller;

import kup.get.entity.energy.*;
import kup.get.service.Services;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private final Services services;

    public UserRestController(Services services) {
        this.services = services;
    }

    @PostMapping("/addWaybills")
    public ResponseEntity<String> addWaybills(@RequestBody List<Product> products) {
        Waybills waybills = null;
        if (products != null && products.size() > 0)
            waybills = products.get(0).getWaybills();
        if (waybills != null && services.getWaybillsService().checkWaybills(waybills.getId())) {
            return new ResponseEntity<>("\"Накладная с таким номером уже есть\"", HttpStatus.CONFLICT);
        }
        try {
            if (products != null && products.size() > 0) {
                services.getWaybillsService().addWaybills(waybills);
                services.getTypeOfProductService().addAllTypeOfProduct(products.stream().map(Product::getType).collect(Collectors.toList()));
                services.getProductService().addProducts(products);
                return new ResponseEntity<>("\"Накладная успешно добавлена\"", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("\"Что-то пошло не так\"", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("\"Список товаров пуст\"", HttpStatus.CONFLICT);
    }

    @PostMapping("/typeOfProduct")
    public ResponseEntity<TypeOfProduct> getTypeOfProduct(@RequestBody long numberRecord) {
        TypeOfProduct type = services.getTypeOfProductService().getProductOfRecord(numberRecord);
        if (type != null) {
            return new ResponseEntity<>(type, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @PatchMapping("/typeOfProduct")
    public void addTypeOfProduct(@RequestBody HashMap<String, String> map) {
        TypeOfProduct type = new TypeOfProduct(
                Long.valueOf(map.get("id")),
                map.get("name"),
                map.get("unit")
        );
        services.getTypeOfProductService().addTypeOfProduct(type);
    }
    @PatchMapping("/writeProductToMaster")
    public ResponseEntity<String> writeProductToMaster(@RequestBody HashMap<String, String> map) {
        Long id = Long.valueOf(map.get("id"));
        double count = Double.parseDouble(map.get("count"));
        try {
            Product product = services.getProductService().findProductsById(id);
            if (product.getRemaining() < count)
                return new ResponseEntity<>("\"Такого количества нет на складе\"", HttpStatus.CONFLICT);
            ProductsOnMaster master = new ProductsOnMaster();
            master.setProduct(product);
            master.setNumberRequire(count);
            master.setRemaining(count);
            master.setDateWrittenToMaster(LocalDate.now());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            master.setUser(services.getUserService().getUserByUsername(auth.getName()));
            services.getMasterService().save(master);

            product.setRemaining(product.getRemaining() - count);
            services.getProductService().writeToMaster(product, count);
            return new ResponseEntity<>("\"Товар успешно выписан\"", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("\"Что-то пошло не так\"", HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("/writeOff")
    public ResponseEntity<String> writeOfProduct(@RequestBody HashMap<String, String> map) {
        Long id = Long.parseLong(map.get("id"));
        double count = Double.parseDouble(map.get("count"));
        String comment = map.get("comment");
        try {
            ProductsOnMaster productsOnMaster = services.getMasterService().findById(id);
            if (productsOnMaster.getRemaining() < count)
                return new ResponseEntity<>("\"Такого количества нет на складе\"", HttpStatus.CONFLICT);
            WrittenOfProducts writtenOfProducts = new WrittenOfProducts();
            writtenOfProducts.setProductsOnMaster(productsOnMaster);
            writtenOfProducts.setNumberRequire(count);
            writtenOfProducts.setComment(comment);

            writtenOfProducts.setRemaining(count);
            writtenOfProducts.setDateWrittenOf(LocalDate.now());
            services.getWrittenOfService().writeOf(writtenOfProducts, count);

            productsOnMaster.setRemaining(productsOnMaster.getRemaining() - count);
            services.getMasterService().save(productsOnMaster);
            return new ResponseEntity<>("\"Товар успешно списан\"", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("\"Что-то пошло не так\"", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/reportExcel")
    public ResponseEntity<Resource> getExcelFile(@RequestParam(value = "dateBegin", required = false) String dateBegin,
                                                 @RequestParam(value = "dateEnd", required = false) String dateEnd,
                                                 HttpServletRequest request) throws MalformedURLException {
        LocalDate begin = dateBegin != null ? LocalDate.parse(dateBegin) : null;
        LocalDate end = dateEnd != null ? LocalDate.parse(dateEnd) : null;
        List<WrittenOfProducts> list = services.getWrittenOfService().getAllProducts(begin, end);
        File file = services.getWrittenOfService().getExcelFile(list);
        Resource resource =  new UrlResource("file:" + file.getPath());
        String contentType = request.getServletContext().getMimeType(file.getAbsolutePath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
