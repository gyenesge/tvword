package home.gabe.tvword.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class CampaignType {
    @Id
    private String type;
    private String description;

    public CampaignType() {
    }

    public CampaignType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Helper methods for Thymeleaf templates.
     */
    public boolean isPictureCampaign() {
        return equals(PictureCampaign.CMP_TYPE);
    }

    /**
     * Helper methods for Thymeleaf templates.
     */
    public boolean isTextCampaign() {
        return equals(TextCampaign.CMP_TYPE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignType that = (CampaignType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "CampaignType{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
