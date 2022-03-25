package kup.get.controller;

import kup.get.entity.postgres.security.User;
import kup.get.service.LogService;
import kup.get.service.Services;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final Services services;
    private final LogService logService;

    public AdminController(Services services, LogService logService) {
        this.services = services;
        this.logService = logService;
    }

    @GetMapping("/users")
    public String userMenu(Model model) {
        model.addAttribute("users", services.getUserService().allUsers());
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable(name = "id") Long id, Model model) {
        try {
            model.addAttribute("roles", services.getUserService().allRoles());
            model.addAttribute("user", services.getUserService().findUserById(id));
        } catch (Exception e) {
            model.addAttribute("message", "Пользователя невозможно удалить");
        }
        return "admin/userMenu";
    }

    @GetMapping("/users/new")
    public String addUserPage(Model model) {
        model.addAttribute("roles", services.getUserService().allRoles());
        model.addAttribute("user", new User());
        return "admin/newUser";
    }

    @PutMapping("/users/new")
    public RedirectView addUser(@ModelAttribute(value = "user") User user, RedirectAttributes redirectAttributes) {
        if (services.getUserService().saveUser(user)!=null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан");
        } else {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/users", true);
    }

    @PatchMapping("/users/{id}")
    public RedirectView updateUser(@ModelAttribute(value = "user") @Valid User user,
                                   RedirectAttributes redirectAttributes) {
        if (services.getUserService().saveUser(user)!=null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно изменён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/users", true);
    }

    @DeleteMapping("/users/{id}")
    public RedirectView deleteUser(@PathVariable(name = "id") Long id,
                                   RedirectAttributes redirectAttributes) {
        if (services.getUserService().deleteUser(id)) {
            redirectAttributes.addFlashAttribute("message","Пользователь успешно удалён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Пользователя нельзя удалить");
        }
        return new RedirectView("/admin/users", true);
    }

    @GetMapping("/log")
    public String log(@RequestParam(value = "dateBegin", required = false) String dateBegin,
                      @RequestParam(value = "dateEnd", required = false) String dateEnd,
                      Model model) {
        LocalDate begin = dateBegin != null && !dateBegin.isEmpty() ? LocalDate.parse(dateBegin) : null;
        LocalDate end = dateEnd != null && !dateEnd.isEmpty() ? LocalDate.parse(dateEnd) : null;
        model.addAttribute("logs", logService.findAll(begin, end));
        return "admin/log";
    }
}
