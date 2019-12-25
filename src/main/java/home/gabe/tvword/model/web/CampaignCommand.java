package home.gabe.tvword.model.web;

import home.gabe.tvword.model.CampaignType;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.Status;
import home.gabe.tvword.model.TextCampaign;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CampaignCommand {

    private Long id;

    private CampaignType type;

    private String name;
    private LocalDateTime start;
    private LocalDateTime expiry;

    private Display[] displays;
    private Boolean[] displayEnablement;

    private Status status = Status.ACTIVE;

    private String text;

    private String bkgColor;

    private String textColor;

    public void initDisplays(Set<Display> all, Set<Display> enabled) {
        if (all == null || (enabled != null && all.size() < enabled.size())) {
            throw new IllegalArgumentException("Incosistent data for setting display enablement.");
        }

        displays = new Display[all.size()];
        displayEnablement = new Boolean[all.size()];
        int i = 0;
        for (Display display : all) {
            displays[i] = display;
            displayEnablement[i] = (enabled != null && enabled.contains(display));
            i++;
        }
    }

    public void initDisplays(Set<Display> all) {
        if (all == null) {
            throw new IllegalArgumentException("Incosistent data for setting display enablement.");
        }

        displays = new Display[all.size()];
        int i = 0;
        for (Display display : all) {
            displays[i] = display;
            i++;
        }
    }

    public TextCampaign toTextCampaign() {
        TextCampaign textCampaign = new TextCampaign();
        textCampaign.setId(id);
        textCampaign.setName(name);
        textCampaign.setText(text);
        textCampaign.setTextColor(textColor);
        textCampaign.setBkgColor(bkgColor);
        textCampaign.setStatus(status);
        textCampaign.setExpiry(expiry);
        textCampaign.setStart(start);
        Set<Display> enabled = textCampaign.getDisplays();
        for (int i = 0; i < displays.length; i++) {
            if (i < displayEnablement.length && displayEnablement[i] != null && displayEnablement[i]) {
                enabled.add(displays[i]);
                displays[i].getCampaigns().add(textCampaign);
            }
        }
        return textCampaign;
    }

    public boolean isDisplaySelected() {
        if (displayEnablement == null || displayEnablement.length == 0)
            return false;
        for (int i = 0; i < displayEnablement.length; i++) {
            if (displayEnablement[i] != null && displayEnablement[i])
                return true;
        }
        return false;
    }
}
