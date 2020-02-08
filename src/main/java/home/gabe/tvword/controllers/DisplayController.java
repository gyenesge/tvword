package home.gabe.tvword.controllers;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.Campaign;
import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.web.CampaignDataCommand;
import home.gabe.tvword.model.web.CreateDisplayCommand;
import home.gabe.tvword.model.web.ModifyDisplayCommand;
import home.gabe.tvword.services.AuditService;
import home.gabe.tvword.services.CampaignService;
import home.gabe.tvword.services.DisplayService;
import lombok.extern.slf4j.Slf4j;
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
    private AuditService auditService;

    public DisplayController(DisplayService displayService, CampaignService campaignService, CampaignSelector campaignSelector, AuditService auditService) {
        this.displayService = displayService;
        this.campaignService = campaignService;
        this.campaignSelector = campaignSelector;
        this.auditService = auditService;
    }

    @GetMapping("/displays/start.json")
    public String startDataDisplay(Model model,
                                   @CookieValue(value = "autostart", defaultValue = "false", required = false) boolean autostart,
                                   @RequestParam(value = "forcestart", defaultValue = "false", required = false) boolean forcestart,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:startDataDisplay()", wrapper);

        Display display = wrapper.getDisplay();

        response.addCookie(createBooleanCookie(COOKIE_AUTOSTART, autostart));

        if (autostart) {
            log.info("Autostart activated for {} display.", display.getName());
        }

        if (autostart || forcestart) {
            return "displays/campaignLoader";
        }

        model.addAttribute("autostart", autostart);
        model.addAttribute("display", display);

        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CONFIG_PAGE);
        return "displays/displayconfig";
    }

    private Cookie createBooleanCookie(String name, boolean value) {
        Cookie cookie = new Cookie(COOKIE_AUTOSTART, String.valueOf(value));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_AGE);
        if (!value)
            cookie.setMaxAge(0);
        return cookie;
    }

    @GetMapping("/displays/next.json")
    public @ResponseBody
    CampaignDataCommand getNextCampaignData(Principal principal,
                                            HttpServletRequest request) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:getNextCampaignData()", wrapper);

        Display display = wrapper.getDisplay();

        Set<Campaign> campaigns = campaignService.findByDisplay(display);

        Campaign campaign = campaignSelector.next(campaigns, request.getSession());

        if (campaign == null) {
            log.warn("Campaign is not available for display {} - {}.", display.getId(), display.getName());
            CampaignDataCommand empty = CampaignDataCommand.empty();
            empty.setText(display.getId() + " - " + display.getName());
            return empty;
        }

        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_EXECUTE_CAMPAIGN, campaign.getId());
        return CampaignDataCommand.fromCampaign(campaign);
    }


    @GetMapping("/displays/config")
    public String getConfigPage(Principal principal,
                                Model model,
                                @CookieValue(value = "autostart", defaultValue = "false", required = false) boolean autostart) {

        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:getConfigPage()", wrapper);

        Display display = wrapper.getDisplay();

        model.addAttribute("autostart", autostart);
        model.addAttribute("display", display);

        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CONFIG_PAGE);
        return "displays/displayconfig";
    }


    @GetMapping("/admin/displays")
    public String getDisplayIndex(Model model, @RequestParam(required = false, defaultValue = "false") Boolean showDeleted, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:getDisplayIndex({})", wrapper, showDeleted);

        Set<Display> displays = displayService.findAll(showDeleted);
        model.addAttribute("displays", displays);
        model.addAttribute("checked", showDeleted);
        return "admin/displays";
    }

    @GetMapping("/admin/displays/create")
    public String getCreateDisplay(Model model, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:getCreateDisplay()", wrapper);

        model.addAttribute("createDisplayCommand", new CreateDisplayCommand());
        return "admin/createdisplay";
    }

    @PostMapping("/admin/displays/createprocess")
    public String processCreateDisplay(@ModelAttribute CreateDisplayCommand command, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:processCreateDisplay({})", wrapper, command);

        if (!command.getPassword1().equals(command.getPassword2()))
            throw new IllegalArgumentException("Password missmatch.");
        Display display = displayService.register(command.getName(), command.getNote(), command.getPassword1());

        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CREATE_DISPLAY, display.getId());
        return "redirect:/admin/displays";
    }

    @GetMapping("/admin/displays/{id}/modify")
    public String getModifyDisplay(Model model, @PathVariable Long id, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:getModifyDisplay({})", wrapper, id);

        Display display = displayService.findById(id);

        ModifyDisplayCommand command = new ModifyDisplayCommand(id);
        command.setNote(display.getNote());
        command.setStatus(display.getStatus().getStatusCode());

        model.addAttribute("display", display);
        model.addAttribute("command", command);
        return "admin/modifydisplay";
    }

    @PostMapping("/admin/displays/modifyprocess")
    public String processModifyDisplay(@ModelAttribute ModifyDisplayCommand command, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: DC:processModify({})", wrapper, command);

        if (command.getId() == null)
            throw new IllegalArgumentException("Invalid command object. Id is not defined.");
        if (!command.getPassword1().equals(command.getPassword2()))
            throw new IllegalArgumentException("Password missmatch.");
        displayService.update(command);
        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_MODIFY_DISPLAY, command.getId());
        return "redirect:/admin/displays";
    }


}
