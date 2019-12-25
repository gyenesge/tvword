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

    public static boolean validateColorCode(String colorCode) {
        if (colorCode == null)
            return false;
        return colorCode.matches("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
    }

    public static void main(String[] args) {
        System.out.println("asd " + validateColorCode("asd"));
        System.out.println("0af " + validateColorCode("0af"));
        System.out.println("#0af " + validateColorCode("#0af"));
        System.out.println("0af " + validateColorCode("0af"));
        System.out.println("#23 " + validateColorCode("#23"));
        System.out.println("#3456 " + validateColorCode("#3456"));
        System.out.println("#2233fF " + validateColorCode("#2233fF"));
        System.out.println("#aAbBcC " + validateColorCode("#aAbBcC"));
        System.out.println("#klm019 " + validateColorCode("#klm019"));
        System.out.println("2233fF " + validateColorCode("2233fF"));
    }
}
