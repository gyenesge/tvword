package home.gabe.tvword.model.web;

import home.gabe.tvword.model.Status;
import lombok.Data;

@Data
public class CampaignFilterCommand {
    public final static Long ALL_DISPLAYS = -1L;
    public final static String ALL_TYPE = "all";
    public final static String ALL_STATUS = "all";

    private Long displayId;
    private String status;
    private String type;

    public static CampaignFilterCommand getDefault() {
        CampaignFilterCommand command = new CampaignFilterCommand();
        command.displayId = ALL_DISPLAYS;
        command.status = Status.ACTIVE.getStatusCode();
        command.type = ALL_TYPE;
        return command;
    }
}
