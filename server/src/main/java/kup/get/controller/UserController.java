package kup.get.controller;

import kup.get.entity.postgres.energy.Product;
import kup.get.service.Services;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final Services services;

    public UserController(Services services) {
        this.services = services;
    }

    @GetMapping("/writeToMaster")
    public String writeToMaster(@RequestParam(value = "numberWaybills", required = false) Long numberWaybills,
                                @RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "numberRequest", required = false) Long numberRequest,
                                Model model) {
        List<Product> products = services.getProductService().searchOfProducts(numberWaybills, numberRequest, name);
        model.addAttribute("products", products);
        return "user/writeToMaster";
    }

    @GetMapping("/writtenOff")
    public String writtenOff(@RequestParam(value = "numberWaybills", required = false) Long numberWaybills,
                             @RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "numberRequest", required = false) Long numberRequest,
                             Model model) {
        model.addAttribute("products", services.getMasterService().findAll(numberWaybills, numberRequest, name));
        return "user/writeOff";
    }

    @GetMapping("/report")
    public String report(@RequestParam(value = "dateBegin", required = false) String dateBegin,
                         @RequestParam(value = "dateEnd", required = false) String dateEnd,
                         HttpServletRequest request,
                         Model model) {
        LocalDate begin = dateBegin != null && !dateBegin.isEmpty() ? LocalDate.parse(dateBegin) : null;
        LocalDate end = dateEnd != null && !dateEnd.isEmpty() ? LocalDate.parse(dateEnd) : null;
        if(request.isUserInRole("ROLE_ADMIN")){
            model.addAttribute("products", services.getWrittenOfService().getAllProducts(begin, end));
        } else if(request.isUserInRole("ROLE_USER")){
            model.addAttribute("products", services.getWrittenOfService().getProducts(begin, end));
        }
        return "user/report";
    }
}
