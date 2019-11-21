package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.services.CampaignService;
import home.gabe.tvword.services.DisplayPrincipal;
import home.gabe.tvword.services.DisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Set;

@Slf4j
@Controller
public class DisplayController {
    public final static int COOKIE_AGE = 365 * 24 * 60 * 60;

    public final static String COOKIE_AUTOSTART = "autostart";

    private DisplayService displayService;
    private CampaignService campaignService;
    private CampaignSelector campaignSelector;

    public DisplayController(DisplayService displayService, CampaignService campaignService, CampaignSelector campaignSelector) {
        this.displayService = displayService;
        this.campaignService = campaignService;
        this.campaignSelector = campaignSelector;
    }

    static Display getDisplay(Principal principal) {
        DisplayPrincipal displayPrincipal = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            Object o = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (!(o instanceof DisplayPrincipal)) {
                log.warn("Invalid principal object for this method call {}.", o.getClass().getName());
                throw new AccessDeniedException("Only Display users can use this service: " + o.getClass().getName());
            }
            displayPrincipal = (DisplayPrincipal) o;
        } else {
            log.warn("Unknown principal class in session {}.", principal.getClass().getName());
            throw new AccessDeniedException("Unknown principal class in session: " + principal.getClass().getName());
        }
        return displayPrincipal.getDisplay();
    }

    @GetMapping("/displays/start")
    public String startDisplay(Model model,
                               @CookieValue(value = "autostart", defaultValue = "false", required = false) boolean autostart,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Principal principal) {
        Display display = getDisplay(principal);

        model.addAttribute("autostart", autostart);
        model.addAttribute("display", display);

        response.addCookie(createBooleanCookie(COOKIE_AUTOSTART, autostart));

        if (autostart) {
            log.info("Autostart activated for {} display.", display.getName());
            return "redirect:/displays/next";
        }

        return "/displays/displayconfig";
    }

    private Cookie createBooleanCookie(String name, boolean value) {
        Cookie cookie = new Cookie(COOKIE_AUTOSTART, String.valueOf(value));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_AGE);
        if (!value)
            cookie.setMaxAge(0);
        return cookie;
    }

    @GetMapping("/displays/next")
    public String getNextCampaign(Principal principal,
                                  HttpServletRequest request) {
        Display display = getDisplay(principal);

        Set<Campaign> campaigns = campaignService.findByDisplay(display);

        Campaign campaign = campaignSelector.next(campaigns, request.getSession());

        if (campaign == null) {
            log.info("There is no valid campaign for display {}.", display);
            return "/displays/no_campaign";
        }

        return "redirect:/campaigns/" + campaign.getId();
    }

    @GetMapping("/displays/config")
    public String getConfigPage(Principal principal,
                                Model model,
                                @CookieValue(value = "autostart", defaultValue = "false", required = false) boolean autostart) {

        Display display = getDisplay(principal);

        model.addAttribute("autostart", autostart);
        model.addAttribute("display", display);

        return "/displays/displayconfig";
    }


}
