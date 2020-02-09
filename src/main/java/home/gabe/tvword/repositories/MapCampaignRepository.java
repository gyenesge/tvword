package home.gabe.tvword.repositories;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.CampaignType;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.Status;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("map")
public class MapCampaignRepository implements CampaignRepository {
    private Map<Long, Campaign> campaigns = new HashMap<>();
    private Long autoId = Long.valueOf(1);

    @Override
    public Campaign save(Campaign campaign) {
        campaign.setId(autoId);
        campaigns.put(autoId, campaign);
        autoId++;
        return campaign;
    }

    @Override
    public Set<Campaign> findAll() {

        return new HashSet<>(campaigns.values());
    }

    @Override
    public Iterable<Campaign> findAllById(Iterable<Long> iterable) {
        Set<Campaign> result = new HashSet<>();
        for (Long id : iterable) {
            Optional<Campaign> optional = findById(id);
            if (optional.isPresent())
                result.add(optional.get());
        }
        return result;
    }

    @Override
    public long count() {
        return campaigns.size();
    }

    @Override
    public void deleteById(Long id) {
        campaigns.remove(id);
    }

    @Override
    public Set<Campaign> findByDisplays(Display display) {
        Set<Campaign> matches = new HashSet<>();

        campaigns.values().stream().
                filter(campaign -> campaign.getDisplays().stream()
                        .anyMatch(disp -> disp.getId() == display.getId()))
                .forEach(campaign -> matches.add(campaign));

        return matches;
    }

    @Override
    public List<Campaign> findAllByDisplaysAndStatusAndType(Display display, Status status, CampaignType ctype) {
        List<Campaign> result = new ArrayList<>();
        for (Campaign campaign : campaigns.values()) {
            if ((display == null || campaign.getDisplays().contains(display)) &&
                    (status == null || campaign.getStatus().equals(status)) &&
                    (ctype == null || campaign.getType().equals(ctype))) {
                result.add(campaign);
            }
        }
        return result;
    }

    @Override
    public void delete(Campaign campaign) {
        campaigns.remove(campaign.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends Campaign> iterable) {
        for (Campaign campaign : iterable)
            campaigns.remove(campaign.getId());
    }

    @Override
    public void deleteAll() {
        campaigns.clear();
    }

    @Override
    public <S extends Campaign> Iterable<S> saveAll(Iterable<S> iterable) {
        for (Campaign campaign : iterable)
            campaigns.put(campaign.getId(), campaign);
        return iterable;
    }

    @Override
    public Optional<Campaign> findById(Long id) {
        Campaign campaign = campaigns.get(id);
        return campaign == null ? Optional.empty() : Optional.of(campaign);
    }

    @Override
    public boolean existsById(Long id) {
        return campaigns.containsKey(id);
    }
}
