package home.gabe.tvword.services;

import home.gabe.tvword.controllers.CampaignSelector;
import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.web.CampaignCommand;
import home.gabe.tvword.repositories.CampaignRepository;
import home.gabe.tvword.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CampaignServiceImpl implements CampaignService {

    private CampaignRepository campaignRepository;
    private CampaignSelector campaignSelector;
    private UserRepository userRepository;

    public CampaignServiceImpl(CampaignRepository campaignRepository, CampaignSelector campaignSelector, UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.campaignSelector = campaignSelector;
        this.userRepository = userRepository;
    }

    @Override
    public Campaign create(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public Set<Campaign> findAll() {

        Iterable<Campaign> iterable = campaignRepository.findAll();
        if (iterable instanceof Set)
            return (Set<Campaign>) iterable;

        Set<Campaign> result = new HashSet<>();
        iterable.forEach(campaign -> {
            result.add(campaign);
        });
        return result;
    }

    @Override
    public Set<Campaign> findByDisplay(Display display) {

        return campaignRepository.findByDisplays(display);
    }

    @Override
    public Set<Campaign> findByDisplay(long displayId) {
        Optional<User> user = userRepository.findById(displayId);
        if (user.isEmpty() || !(user.get() instanceof Display))
            throw new IllegalArgumentException("Unknown display with ID: " + displayId);
        return findByDisplay((Display) user.get());
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

    @Override
    public Campaign update(CampaignCommand campaignCommand) {
        Optional<Campaign> oCampaign = campaignRepository.findById(campaignCommand.getId());
        if (oCampaign.isEmpty())
            throw new IllegalArgumentException("Invalid campaign id: " + campaignCommand.getId());
        Campaign campaign = oCampaign.get();

        campaignCommand.update(campaign);

        return campaignRepository.save(campaign);
    }
}
