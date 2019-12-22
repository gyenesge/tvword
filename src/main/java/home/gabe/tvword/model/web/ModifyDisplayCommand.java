package home.gabe.tvword.model.web;

import lombok.Data;

@Data
public class ModifyDisplayCommand {
    private Long id;
    private String note;
    private String password1;
    private String password2;
    private String status;

    public ModifyDisplayCommand() {
        super();
    }

    public ModifyDisplayCommand(Long id) {
        super();

        this.id = id;
    }
}
