package io.github.kaurami.wems.controller;

import io.github.kaurami.wems.exception.RateLimitExceededException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы успешно вышли из системы.");
        }
        return "login";
    }

    @GetMapping("/locked")
    public String lockedPage() {
        throw new RateLimitExceededException("Слишком много попыток входа. Пожалуйста, подождите минуту и попробуйте снова.");
    }
}
