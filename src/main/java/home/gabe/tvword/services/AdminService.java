package home.gabe.tvword.services;

import home.gabe.tvword.model.User;
import home.gabe.tvword.model.web.PasswordCommand;

public interface AdminService {
    void changePassword(User user, PasswordCommand command);
}
