package home.gabe.tvword.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class PictureCampaign extends Campaign {
    public final static CampaignType CMP_TYPE = new CampaignType("P", "Picture campaign with a full screen picture.");

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

    public PictureCampaign() {
        super();
        super.setType(CMP_TYPE);
    }
}
