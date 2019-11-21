package home.gabe.tvword.services;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;

import java.util.Set;

public interface CampaignService {

    Campaign create(Campaign campaign);

    Iterable<Campaign> findAll();

    Set<Campaign> findByDisplay(Display display);

    void delete(Campaign campaign);

    Campaign findById(Long id);
}
