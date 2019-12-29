package home.gabe.tvword.model.web;

import home.gabe.tvword.model.*;
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

    private String status = Status.ACTIVE.getStatusCode();

    private String text;

    private String bkgColor;

    private String textColor;

    private Image image;

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

        update(textCampaign);

        textCampaign.setText(text);
        textCampaign.setTextColor(textColor);
        textCampaign.setBkgColor(bkgColor);

        return textCampaign;
    }

    public Campaign toPictureCampaign() {
        PictureCampaign pictureCampaign = new PictureCampaign();

        update(pictureCampaign);

        pictureCampaign.setImage(image);

        return pictureCampaign;

    }

    private void loadDisplays(Campaign campaign) {
        Set<Display> enabled = campaign.getDisplays();

        //clear up first
        for (Display display : enabled) {
            display.getCampaigns().remove(campaign);
        }
        enabled.clear();

        //then set new assigments
        for (int i = 0; i < displays.length; i++) {
            if (i < displayEnablement.length && displayEnablement[i] != null && displayEnablement[i]) {
                enabled.add(displays[i]);
                this.displays[i].getCampaigns().add(campaign);
            }
        }
    }

    public void populate(Campaign campaign, Set<Display> displays) {
        id = campaign.getId();
        name = campaign.getName();
        status = campaign.getStatus().getStatusCode();
        expiry = campaign.getExpiry();
        start = campaign.getStart();
        type = campaign.getType();
        initDisplays(displays, campaign.getDisplays());
        if (campaign instanceof TextCampaign) {
            text = ((TextCampaign) campaign).getText();
            textColor = ((TextCampaign) campaign).getTextColor();
            bkgColor = ((TextCampaign) campaign).getBkgColor();
        } else if (campaign instanceof PictureCampaign) {
            image = ((PictureCampaign) campaign).getImage();
        } else
            throw new IllegalArgumentException("Invalid campaign type: " + campaign.getClass().getName());
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

    public void update(Campaign campaign) {
        campaign.setId(id);
        campaign.setName(name);
        campaign.setStart(start);
        campaign.setExpiry(expiry);
        campaign.setStatus(Status.parse(status));
        loadDisplays(campaign);
    }

}
