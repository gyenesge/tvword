package home.gabe.tvword.model.web;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.TextCampaign;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignDataCommand {
    private Long id;
    private String type;
    private String name;
    private String text;
    private String bkgColor;
    private String textColor;

    public static CampaignDataCommand fromCampaign(Campaign campaign) {
        CampaignDataCommand command = new CampaignDataCommand();
        command.id = campaign.getId();
        command.type = campaign.getType().getType();
        command.name = campaign.getName();
        if (campaign instanceof TextCampaign) {
            command.text = ((TextCampaign) campaign).getText();
            command.bkgColor = ((TextCampaign) campaign).getBkgColor();
            command.textColor = ((TextCampaign) campaign).getTextColor();
        }
        return command;
    }

    public static CampaignDataCommand empty() {
        CampaignDataCommand command = new CampaignDataCommand();
        command.id = -1L;
        command.type = TextCampaign.CMP_TYPE.getType();
        command.name = "no campaign";
        command.text = "N/A";
        command.bkgColor = "#000";
        command.textColor = "#999";
        return command;
    }
}
