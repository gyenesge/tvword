package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.User;
import home.gabe.tvword.services.BaseUserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

@Slf4j
public class UserPrincipalWrapper {

    private Principal principal;
    private User user;

    public UserPrincipalWrapper(Principal principal) {
        this.principal = principal;
    }

    public User getUser() {

        if (user != null) // cached user object
            return user;

        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            log.warn("Unknown principal class in session {}.", principal.getClass().getName());
            throw new AccessDeniedException("Unknown principal class in session: " + principal.getClass().getName());
        }

        Object o = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (!(o instanceof BaseUserPrincipal)) {
            log.warn("Invalid principal object for this method call {}.", o.getClass().getName());
            throw new AccessDeniedException("Only authenticated users can use this service: " + o.getClass().getName());
        }

        this.user = ((BaseUserPrincipal) o).getUser();
        return this.user;
    }

    public Display getDisplay() {
        User user = getUser();
        if (user instanceof Display)
            return (Display) user;
        else
            throw new AccessDeniedException("Only Display users can use this service: " + user.getClass().getName());
    }


    @Override
    public String toString() {
        User user = getUser();
        if (user == null)
            return "null\\null";
        return user.getRole().getId() + '\\' + user.getName();
    }
}
