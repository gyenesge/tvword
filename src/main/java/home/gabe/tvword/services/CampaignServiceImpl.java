package home.gabe.tvword.services;

import home.gabe.tvword.controllers.CampaignSelector;
import home.gabe.tvword.model.*;
import home.gabe.tvword.model.web.CampaignCommand;
import home.gabe.tvword.model.web.CampaignFilterCommand;
import home.gabe.tvword.repositories.CampaignRepository;
import home.gabe.tvword.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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
    public List<Campaign> findByFilter(CampaignFilterCommand filter) {
        Display display = null;
        if (filter.getDisplayId() != null && filter.getDisplayId() != CampaignFilterCommand.ALL_DISPLAYS) {
            Optional<User> user = userRepository.findById(filter.getDisplayId());
            if (user.isEmpty() || !(user.get() instanceof Display))
                throw new IllegalArgumentException("Unknown display with ID: " + filter.getDisplayId());
            display = (Display) user.get();
        }

        Status status = null;
        if (filter.getStatus() != null && !filter.getStatus().equals(CampaignFilterCommand.ALL_STATUS)) {
            status = Status.parse(filter.getStatus());
        }

        CampaignType type = null;
        if (filter.getType() != null && !filter.getType().equals(CampaignFilterCommand.ALL_TYPE)) {
            type = CampaignType.parse(filter.getType());
        }

        return campaignRepository.findAllByDisplaysAndStatusAndType(display, status, type);
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
