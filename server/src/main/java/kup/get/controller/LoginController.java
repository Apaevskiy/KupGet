package kup.get.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/")
    public String authorizationPage() {
        return "redirect: /login";
    }
}
