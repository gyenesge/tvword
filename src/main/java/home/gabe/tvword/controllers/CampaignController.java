package home.gabe.tvword.controllers;

import home.gabe.tvword.model.*;
import home.gabe.tvword.model.web.CampaignCommand;
import home.gabe.tvword.services.CampaignService;
import home.gabe.tvword.services.DisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@Controller
public class CampaignController {

    private CampaignService campaignService;
    private DisplayService displayService;

    public CampaignController(CampaignService campaignService, DisplayService displayService) {
        this.campaignService = campaignService;
        this.displayService = displayService;
    }

    @RequestMapping("/campaigns/{id}")
    public String getCampaign(@PathVariable Long id,
                              @CookieValue(value = "fullScreen", defaultValue = "false", required = false) boolean fullScreen,
                              Principal principal,
                              Model model) {

        Campaign campaign = campaignService.findById(id);
        if (campaign == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown campaign ID: " + id);

        if (!campaign.getStatus().equals(Status.ACTIVE))
            throw new TVWordException("The selected campaign is not active: " + id, TVWordException.EC_CAMPAIGN_NOTACTIVE);

        //validate permission and consistence (e.g. a campaign can be displayed only for authenticated device)
        Display activeDisplay = DisplayController.getDisplay(principal);
        if (!campaign.getDisplays().contains(activeDisplay))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The campaing " + campaign.getId() + " is not enabled for display " + activeDisplay.getId());

        model.addAttribute("fullScreen", fullScreen);
        model.addAttribute("campaign", campaign);

        if (campaign instanceof TextCampaign)
            return "/campaigns/textcampaign.html";
        else if (campaign instanceof PictureCampaign)
            return "/campaigns/picturecampaign.html";

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested campaign method is not supported: " + campaign.getClass().getName());
    }

    @GetMapping("/campaigns/{id}/image")
    public void showCampaignImage(@PathVariable String id, HttpServletResponse response) throws IOException {

        Campaign campaign = campaignService.findById(Long.parseLong(id));
        if (campaign == null || !(campaign instanceof PictureCampaign))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The request campaign does not exist or it is not a picture campaign. ID: " + id);

        Image image = ((PictureCampaign) campaign).getImage();
        response.setContentType(image.getFormat());
        InputStream is = new ByteArrayInputStream(image.getContent());
        is.transferTo(response.getOutputStream());
    }


    @RequestMapping("/admin/campaigns")
    public String getCampaignIndex(Model model, @RequestParam(required = false) String displayId) {
        Iterable<Campaign> campaigns = null;
        Long id = null;
        if (displayId == null)
            campaigns = campaignService.findAll();
        else {
            id = Long.parseLong(displayId);
            campaigns = campaignService.findByDisplay(id);
        }

        model.addAttribute("campaigns", campaigns);
        model.addAttribute("displays", displayService.findAll(false));
        model.addAttribute("filterDisplay", id);
        return "/admin/campaigns";
    }

    @RequestMapping("/admin/campaigns/create/text")
    public String getTextCampaignCreate(Model model) {
        CampaignCommand campaign = new CampaignCommand();
        //set default values
        campaign.setType(TextCampaign.CMP_TYPE);
        campaign.setStart(LocalDateTime.now());
        campaign.setExpiry(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
        campaign.setBkgColor("#FFF");
        campaign.setTextColor("#000");
        campaign.setText("Lorem ipsum, dolor sit amet...");

        Set<Display> displays = displayService.findAll(false);

        campaign.initDisplays(displays, null);

        model.addAttribute("campaign", campaign);
        model.addAttribute("displays", displays);
        return "/admin/createtextcam";
    }

    @PostMapping("/admin/campaigns/createtextprocess")
    public String processCreateTextCampaign(Model model, @ModelAttribute CampaignCommand command) {
        if (command.getName() == null || command.getName().trim().length() == 0)
            throw new TVWordException("Name of campaign is mandatory for text campaign.", TVWordException.EC_NAME_MISSING);
        if (command.getStart() == null || command.getStart().isBefore(LocalDate.now().atStartOfDay()))
            throw new TVWordException("Invalid start date.", TVWordException.EC_INVALID_START);
        if (command.getExpiry() == null || command.getExpiry().isBefore(command.getStart()))
            throw new TVWordException("Invalid start date.", TVWordException.EC_INVALID_EXPIRY);
        if (command.getText() == null || command.getText().trim().length() == 0)
            throw new TVWordException("Text message is mandatory for text campaign.", TVWordException.EC_TEXT_MISSING);
        if (!TextCampaign.validateColorCode(command.getTextColor()) || !TextCampaign.validateColorCode(command.getBkgColor()))
            throw new TVWordException("Invalid color code.", TVWordException.EC_INVALID_COLORCODE);
        if (!command.isDisplaySelected())
            throw new TVWordException("At least one display must be selected for the campaign.", TVWordException.EC_DISPLAY_NOT_ENABLED);

        command.initDisplays(displayService.findAll(false));
        campaignService.create(command.toTextCampaign());
        return "redirect:/admin/campaigns";
    }

    @RequestMapping("/admin/campaigns/create/picture")
    public String getPictureCampaignCreate() {
        return "/admin/createpicturecam";
    }

    @RequestMapping("/admin/campaigns/{id}/modify")
    public String getModifyCampaign(Model model, @PathVariable Long id) {
        model.addAttribute("campaign", campaignService.findById(id));
        model.addAttribute("displays", displayService.findAll(false));
        return "/admin/modifycam";
    }
}
