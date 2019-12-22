package home.gabe.tvword.services;

import home.gabe.tvword.model.Admin;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.Status;
import home.gabe.tvword.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class BaseUserPrincipal implements UserDetails {
    protected final User user;

    public BaseUserPrincipal(User user) {
        if (user == null)
            throw new NullPointerException("User principal cannot be initialied with null. ");
        this.user = user;
    }

    public static BaseUserPrincipal createPrincipal(User user) {
        if (user == null)
            throw new NullPointerException();
        if (user instanceof Display)
            return new DisplayPrincipal(user);
        if (user instanceof Admin)
            return new AdminPrincipal(user);

        throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // USER role is granted to everyone
        authorities.add(new SimpleGrantedAuthority(user.getRole().getSpringRoleName()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus().equals(Status.ACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(Status.ACTIVE);
    }

    public User getUser() {
        return user;
    }
}
