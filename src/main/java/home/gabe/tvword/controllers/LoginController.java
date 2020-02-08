package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Display;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String getDisplayLoginPage(Model model, @RequestParam(value = "error", defaultValue = "false") boolean error, Principal principal) {
        UserPrincipalWrapper wrapper = principal == null ? null : new UserPrincipalWrapper(principal);
        log.info("{}: LC:getDisplayLoginPage({})", wrapper, error);

        model.addAttribute("error", error);
        return "login_display";
    }

    @RequestMapping("/start")
    public String getStartPage(Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: LC:getStartPage()", wrapper);

        if (wrapper.getUser() instanceof Display)
            return "redirect:/displays/start.json";
        return "redirect:/admin/displays";
    }

}
