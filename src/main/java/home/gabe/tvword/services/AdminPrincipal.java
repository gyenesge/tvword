package home.gabe.tvword.services;

import home.gabe.tvword.model.Admin;
import home.gabe.tvword.model.User;

public class AdminPrincipal extends BaseUserPrincipal {
    public AdminPrincipal(User user) {
        super(user);
        if (!(user instanceof Admin))
            throw new IllegalArgumentException("AdminUserPrincipal cannot be initialied with " + user.getClass().getName());
    }

    public Admin getAdmin() {
        return (Admin) user;
    }
}
