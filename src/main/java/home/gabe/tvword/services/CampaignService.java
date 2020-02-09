package home.gabe.tvword.services;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.web.CampaignCommand;
import home.gabe.tvword.model.web.CampaignFilterCommand;

import java.util.List;
import java.util.Set;

public interface CampaignService {

    Campaign create(Campaign campaign);

    Campaign update(CampaignCommand campaignCommand);

    Set<Campaign> findAll();

    Set<Campaign> findByDisplay(Display display);

    Set<Campaign> findByDisplay(long displayId);

    List<Campaign> findByFilter(CampaignFilterCommand filter);

    void delete(Campaign campaign);

    Campaign findById(Long id);
}
