package home.gabe.tvword.services;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.web.CampaignCommand;

import java.util.Set;

public interface CampaignService {

    Campaign create(Campaign campaign);

    Campaign update(CampaignCommand campaignCommand);

    Set<Campaign> findAll();

    Set<Campaign> findByDisplay(Display display);

    Set<Campaign> findByDisplay(long displayId);

    void delete(Campaign campaign);

    Campaign findById(Long id);
}
