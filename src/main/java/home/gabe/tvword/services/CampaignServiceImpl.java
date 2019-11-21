package home.gabe.tvword.services;

import home.gabe.tvword.controllers.CampaignSelector;
import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.repositories.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CampaignServiceImpl implements CampaignService {

    private CampaignRepository campaignRepository;
    private CampaignSelector campaignSelector;

    public CampaignServiceImpl(CampaignRepository campaignRepository, CampaignSelector campaignSelector) {
        this.campaignRepository = campaignRepository;
        this.campaignSelector = campaignSelector;
    }

    @Override
    public Campaign create(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public Iterable<Campaign> findAll() {

        return campaignRepository.findAll();
    }

    @Override
    public Set<Campaign> findByDisplay(Display display) {

        return campaignRepository.findByDisplays(display);
    }

    @Override
    public void delete(Campaign campaign) {
        campaignRepository.delete(campaign);
    }

    @Override
    public Campaign findById(Long id) {
        Optional<Campaign> optional = campaignRepository.findById(id);
        return optional.isEmpty() ? null : optional.get();
    }
}
