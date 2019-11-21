package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Campaign;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RoundRobinCampaignSelector implements CampaignSelector {

    private static final String CAMPAIGN_SELECTOR_ROUND_ROBIN = "CAMPAIGN_SELECTOR_ROUND_ROBIN";

    @Override
    public Campaign next(Set<Campaign> campaigns, HttpSession session) {
        if (campaigns == null || campaigns.size() == 0)
            return null;

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Integer> occurences = (Map<Long, Integer>) session.getAttribute(CAMPAIGN_SELECTOR_ROUND_ROBIN);
        if (occurences == null) {
            occurences = new HashMap<>();
            session.setAttribute(CAMPAIGN_SELECTOR_ROUND_ROBIN, occurences);
        }

        int minOccurrences = 0;
        Campaign min = null;
        for (Campaign campaign : campaigns) {

            if (campaign.getStart().isAfter(now)
                    || campaign.getExpiry().isBefore(now))
                continue; // skip expired or not-started campaign

            if (min == null) { // init min value
                min = campaign;
                if (occurences.containsKey(campaign.getId()))
                    minOccurrences = occurences.get(campaign.getId());
            } else if (occurences.containsKey(campaign.getId()) && occurences.get(campaign.getId()) < minOccurrences) { // update min value with less occurences
                minOccurrences = occurences.get(campaign.getId());
                min = campaign;
            } else if (minOccurrences > 0 && !occurences.containsKey(campaign.getId())) { // never displayed campaign comes first
                minOccurrences = 0;
                min = campaign;
            }
        }
        minOccurrences++;
        occurences.put(min.getId(), minOccurrences);
        return min;
    }
}
