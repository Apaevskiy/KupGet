package kup.get.controller;

import kup.get.entity.postgres.energy.Product;
import kup.get.entity.postgres.energy.Waybills;
import kup.get.service.Services;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class WaybillsController {
    private final Services services;

    public WaybillsController(Services services) {
        this.services = services;
    }

    @GetMapping("/user/waybills")
    public String waybills() {
        return "user/waybillsPage";
    }

    @GetMapping("/admin/waybills")
    public String waybillsPage(@RequestParam(value = "waybillsId", required = false) Long waybillsId,
                               @RequestParam(value = "person", required = false) String person,
                               @RequestParam(value = "department", required = false) String department,
                               @RequestParam(value = "dateBefore", required = false) String dateBegin,
                               @RequestParam(value = "dateAfter", required = false) String dateEnd,
                               Model model) {
        LocalDate begin = dateBegin != null && !dateBegin.isEmpty() ? LocalDate.parse(dateBegin) : null;
        LocalDate end = dateEnd != null && !dateEnd.isEmpty() ? LocalDate.parse(dateEnd) : null;
        model.addAttribute("waybills",
                services.getWaybillsService().getSortWaybills(
                        waybillsId,
                        person,
                        department,
                        begin,
                        end
                ));
        return "admin/waybills";
    }

    @GetMapping("/admin/waybills/{id}")
    public String editWaybills(@PathVariable(name = "id") Long id,
                               Model model) {
        model.addAttribute("waybill", services.getWaybillsService().findById(id));
        model.addAttribute("products", services.getProductService().findAllByWaybillsId(id));
        return "admin/waybillsEdit";
    }

    @PatchMapping("/admin/waybills/{id}")
    public RedirectView updateWaybills(@PathVariable(name = "id") Long id,
                                       @ModelAttribute(value = "waybills") Waybills waybills,
                                       RedirectAttributes redirectAttributes) {
        try {
            System.out.println(waybills.getDate());
            services.getWaybillsService().save(waybills);
            redirectAttributes.addFlashAttribute("message", "Накладная успешно обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/waybills", true);
    }
    @DeleteMapping("/admin/waybills/{id}")
    public RedirectView deleteWaybills(@PathVariable(name = "id") Long id,
                                       RedirectAttributes redirectAttributes) {
        try {
            List<Product> products = services.getProductService().findAllByWaybillsId(id);
            boolean check = true;
            for (Product product: products) {
                if (product.getNumberReleased()!=product.getRemaining())
                    check=false;
            }
            if(check){
                for (Product product: products) {
                    services.getProductService().deleteProduct(product.getId());
                }
                services.getWaybillsService().delete(id);
                redirectAttributes.addFlashAttribute("message", "Накладная успешно удалена");
            } else {
                redirectAttributes.addFlashAttribute("error", "Один из товаров уже выписан");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/waybills", true);
    }

    @GetMapping("/admin/waybills/{waybillsId}/newProduct")
    public String newProductOnWaybill(@PathVariable(name = "waybillsId") Long waybillsId,
                                      Model model) {
        model.addAttribute("waybill", waybillsId);
        model.addAttribute("product", new Product());
        return "admin/newProduct";
    }

    @PutMapping("/admin/waybills/{waybillsId}/newProduct")
    public RedirectView addNewProductOnWaybill(@PathVariable(name = "waybillsId") Long waybillsId,
                                               @ModelAttribute(value = "product") Product product,
                                               RedirectAttributes redirectAttributes) {
        Waybills waybills = new Waybills();
        waybills.setId(waybillsId);
        product.setWaybills(waybills);
        if (services.getProductService().addProductToWaybill(product)) {
            redirectAttributes.addFlashAttribute("message", "Товар успешно добавлен");
        } else {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/waybills/" + waybillsId, true);
    }

    @GetMapping("/admin/waybills/{waybillsId}/product/{productId}")
    public String editProductOnWaybill(@PathVariable(name = "waybillsId") Long waybillsId,
                                       @PathVariable(name = "productId") Long productId,
                                       Model model) {
        model.addAttribute("product", services.getProductService().findProductsById(productId));
        return "admin/productEdit";
    }

    @DeleteMapping("/admin/waybills/{waybillsId}/product/{productId}")
    public RedirectView deleteProductOnWaybill(@PathVariable(name = "waybillsId") Long waybillsId,
                                               @PathVariable(name = "productId") Long productId,
                                               RedirectAttributes redirectAttributes) {
        switch (services.getProductService().deleteProduct(productId)) {
            case 0:
                redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
                break;
            case -1:
                redirectAttributes.addFlashAttribute("error", "Нельзя удалить выписанные товары");
                break;
            case 1:
                redirectAttributes.addFlashAttribute("message", "Товар успешно изменён");
        }
        return new RedirectView("/admin/waybills/" + waybillsId, true);
    }

    @PatchMapping("/admin/waybills/{waybillsId}/product/{productId}")
    public RedirectView updateProductOnWaybill(@PathVariable(name = "waybillsId") Long waybillsId,
                                               @PathVariable(name = "productId") Long productId,
                                               @ModelAttribute(value = "product") Product product,
                                               RedirectAttributes redirectAttributes) {
        product.setId(productId);
        switch (services.getProductService().updateProduct(product)) {
            case 0:
                redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
                break;
            case -1:
                redirectAttributes.addFlashAttribute("error", "Нельзя изменить выписанные товары");
                break;
            case 1:
                redirectAttributes.addFlashAttribute("message", "Товар успешно изменён");
        }
        return new RedirectView("/admin/waybills/" + waybillsId, true);
    }
}
