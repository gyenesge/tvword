package home.gabe.tvword.model.web;

import lombok.Data;

@Data
public class PasswordCommand {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
