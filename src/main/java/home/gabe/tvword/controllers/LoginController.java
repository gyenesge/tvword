package home.gabe.tvword.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class LoginController {

    @RequestMapping("/login/display")
    public String getDisplayLoginPage(Model model, @RequestParam(value = "error", defaultValue = "false") boolean error) {
        //log.info("Login page is loading with error param {}.", error);
        model.addAttribute("error", error);
        return "/login_display";
    }
}
