package kup.get.controller;

import kup.get.entity.postgres.security.User;
import kup.get.service.LogService;
import kup.get.service.security.UserService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Deprecated
public class AdminController {
    private final LogService logService;
    private final UserService userService;

    @GetMapping("/users")
    public String userMenu(Model model) {
        model.addAttribute("users", userService.allUsers());
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable(name = "id") Long id, Model model) {
        try {
            model.addAttribute("roles", userService.allRoles());
            model.addAttribute("user", userService.findUserById(id));
        } catch (Exception e) {
            model.addAttribute("message", "Пользователя невозможно удалить");
        }
        return "admin/userMenu";
    }

    @GetMapping("/users/new")
    public String addUserPage(Model model) {
        model.addAttribute("roles", userService.allRoles());
        model.addAttribute("user", new User());
        return "admin/newUser";
    }

   /* @PutMapping("/users/new")
    public RedirectView addUser(@ModelAttribute(value = "user") User user, RedirectAttributes redirectAttributes) {
        if (userService.saveUser(user)!=null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан");
        } else {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/users", true);
    }

    @PatchMapping("/users/{id}")
    public RedirectView updateUser(@ModelAttribute(value = "user") @Valid User user,
                                   RedirectAttributes redirectAttributes) {
        if (userService.saveUser(user)!=null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно изменён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Что-то пошло не так");
        }
        return new RedirectView("/admin/users", true);
    }

    @DeleteMapping("/users/{id}")
    public RedirectView deleteUser(@PathVariable(name = "id") Long id,
                                   RedirectAttributes redirectAttributes) {
        if (userService.deleteUser(id)) {
            redirectAttributes.addFlashAttribute("message","Пользователь успешно удалён");
        } else {
            redirectAttributes.addFlashAttribute("error", "Пользователя нельзя удалить");
        }
        return new RedirectView("/admin/users", true);
    }*/

    @GetMapping("/log")
    public String log(@RequestParam(value = "dateBegin", required = false) String dateBegin,
                      @RequestParam(value = "dateEnd", required = false) String dateEnd,
                      Model model) {
        LocalDate begin = dateBegin != null && !dateBegin.isEmpty() ? LocalDate.parse(dateBegin) : null;
        LocalDate end = dateEnd != null && !dateEnd.isEmpty() ? LocalDate.parse(dateEnd) : null;
        model.addAttribute("logs", logService.findAllByDateIsBetween(begin, end));
        return "admin/log";
    }
}
