package home.gabe.tvword.model;

import javax.persistence.Entity;

@Entity
public class TextCampaign extends Campaign {
    public final static CampaignType CMP_TYPE = new CampaignType("T", "Text campaign with background and text color.");

    private String text;

    private String bkgColor;

    private String textColor;

    public TextCampaign() {
        super();
        super.setType(CMP_TYPE);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBkgColor() {
        return bkgColor;
    }

    public void setBkgColor(String bkgColor) {
        this.bkgColor = bkgColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "TextCampaign{" +
                toStringSuper() +
                "text='" + text + '\'' +
                ", bkgColor='" + bkgColor + '\'' +
                ", textColor='" + textColor + '\'' +
                '}';
    }
}
