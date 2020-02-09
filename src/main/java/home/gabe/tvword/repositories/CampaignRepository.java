package home.gabe.tvword.repositories;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.CampaignType;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    Set<Campaign> findByDisplays(Display display);

    @Query("SELECT distinct c FROM Campaign c JOIN c.displays d WHERE (:display is null or :display = d) and " +
            "(:status is null or :status = c.status) and (:ctype is null or :ctype = c.type) " +
            "ORDER BY c.name")
    List<Campaign> findAllByDisplaysAndStatusAndType(Display display, Status status, CampaignType ctype);
}
