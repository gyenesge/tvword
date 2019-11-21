package home.gabe.tvword.services;

import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.User;

public class DisplayPrincipal extends BaseUserPrincipal {

    public DisplayPrincipal(User user) {
        super(user);
        if (!(user instanceof Display))
            throw new IllegalArgumentException("DisplayUserPrincipal cannot be initialied with " + user.getClass().getName());
    }

    public Display getDisplay() {
        return (Display) user;
    }
}
