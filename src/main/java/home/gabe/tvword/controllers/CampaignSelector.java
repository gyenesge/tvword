package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Campaign;

import javax.servlet.http.HttpSession;
import java.util.Set;

public interface CampaignSelector {
    Campaign next(Set<Campaign> campaigns, HttpSession session);
}
