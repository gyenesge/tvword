package home.gabe.tvword.model.web;

import lombok.Data;

@Data
public class CreateDisplayCommand {
    private String name;
    private String note;
    private String password1;
    private String password2;
}
