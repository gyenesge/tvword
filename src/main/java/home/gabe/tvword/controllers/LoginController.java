package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.User;
import home.gabe.tvword.services.BaseUserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@Controller
public class LoginController {

    @RequestMapping("/login/display")
    public String getDisplayLoginPage(Model model, @RequestParam(value = "error", defaultValue = "false") boolean error) {
        //log.info("Login page is loading with error param {}.", error);
        model.addAttribute("error", error);
        return "/login_display";
    }

    static User getUser(Principal principal) {
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            log.warn("Unknown principal class in session {}.", principal.getClass().getName());
            throw new AccessDeniedException("Unknown principal class in session: " + principal.getClass().getName());
        }

        Object o = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (!(o instanceof BaseUserPrincipal)) {
            log.warn("Invalid principal object for this method call {}.", o.getClass().getName());
            throw new AccessDeniedException("Only authenticated users can use this service: " + o.getClass().getName());
        }

        return ((BaseUserPrincipal) o).getUser();
    }

    @RequestMapping("/start")
    public String getStartPage(Principal principal) {
        User user = getUser(principal);
        if (user instanceof Display)
            return "redirect:/displays/start";
        return "redirect:/admin/displays";
    }

}
