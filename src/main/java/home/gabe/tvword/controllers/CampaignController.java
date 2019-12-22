package home.gabe.tvword.controllers;

import home.gabe.tvword.model.*;
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
            throw new IllegalStateException("The selected campaign is not active: " + id);

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
    public String getTextCampaignCreate() {
        return "/admin/createtextcam";
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
