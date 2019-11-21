package home.gabe.tvword.repositories;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    Set<Campaign> findByDisplays(Display display);
}
