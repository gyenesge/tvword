package home.gabe.tvword.controllers;

import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.web.CreateDisplayCommand;
import home.gabe.tvword.model.web.ModifyDisplayCommand;
import home.gabe.tvword.services.CampaignService;
import home.gabe.tvword.services.DisplayPrincipal;
import home.gabe.tvword.services.DisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping("/admin/displays")
    public String getDisplayIndex(Model model, @RequestParam(required = false, defaultValue = "false") Boolean showDeleted) {
        Set<Display> displays = displayService.findAll(showDeleted);
        model.addAttribute("displays", displays);
        model.addAttribute("checked", showDeleted);
        return "/admin/displays";
    }

    @RequestMapping("/admin/displays/create")
    public String getCreateDisplay(Model model) {

        model.addAttribute("createDisplayCommand", new CreateDisplayCommand());
        return "/admin/createdisplay";
    }

    @PostMapping("/admin/displays/createprocess")
    public String getCreateDisplayProcess(@ModelAttribute CreateDisplayCommand command) {

        if (!command.getPassword1().equals(command.getPassword2()))
            throw new IllegalArgumentException("Password missmatch.");
        displayService.register(command.getName(), command.getNote(), command.getPassword1());
        return "redirect:/admin/displays";
    }

    @RequestMapping("/admin/displays/{id}/modify")
    public String getModifyDisplay(Model model, @PathVariable Long id) {
        Display display = displayService.findById(id);

        ModifyDisplayCommand command = new ModifyDisplayCommand(id);
        command.setNote(display.getNote());
        command.setStatus(display.getStatus().getStatusCode());

        model.addAttribute("display", display);
        model.addAttribute("command", command);
        return "/admin/modifydisplay";
    }

    @PostMapping("/admin/displays/modifyprocess")
    public String processModify(@ModelAttribute ModifyDisplayCommand command) {
        if (command.getId() == null)
            throw new IllegalArgumentException("Invalid command object. Id is not defined.");
        if (!command.getPassword1().equals(command.getPassword2()))
            throw new IllegalArgumentException("Password missmatch.");
        displayService.update(command);
        return "redirect:/admin/displays";
    }


}
