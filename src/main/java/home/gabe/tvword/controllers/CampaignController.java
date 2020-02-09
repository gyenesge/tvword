package home.gabe.tvword.controllers;

import home.gabe.tvword.model.*;
import home.gabe.tvword.model.web.CampaignCommand;
import home.gabe.tvword.model.web.CampaignFilterCommand;
import home.gabe.tvword.services.AuditService;
import home.gabe.tvword.services.CampaignService;
import home.gabe.tvword.services.DisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class CampaignController {
    private final static String SESSION_CAMPAIGN_FILTER = "admin.campaign.filter";

    private CampaignService campaignService;
    private DisplayService displayService;
    private AuditService auditService;

    public CampaignController(CampaignService campaignService, DisplayService displayService, AuditService auditService) {
        this.campaignService = campaignService;
        this.displayService = displayService;
        this.auditService = auditService;
    }

    @GetMapping(value = {"/campaigns/{id}/image", "/admin/campaigns/{id}/image"})
    public void showCampaignImage(@PathVariable String id, HttpServletResponse response, Principal principal) throws IOException {

        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:showCampaignImage({})", wrapper, id);

        Campaign campaign = campaignService.findById(Long.parseLong(id));
        if (campaign == null || !(campaign instanceof PictureCampaign))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested campaign does not exist or it is not a picture campaign. ID: " + id);

        Image image = ((PictureCampaign) campaign).getImage();
        response.setContentType(image.getFormat());
        InputStream is = new ByteArrayInputStream(image.getContent());
        is.transferTo(response.getOutputStream());
    }


    @GetMapping("/admin/campaigns")
    public String getCampaignIndex(Model model,
                                   @ModelAttribute CampaignFilterCommand filter,
                                   HttpServletRequest request,
                                   Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:getCampaignIndex()", wrapper);

        HttpSession session = request.getSession();
        if (filter == null || filter.getDisplayId() == null) {
            //filter is not initialized
            Object storedFilter = session.getAttribute(SESSION_CAMPAIGN_FILTER);
            if (storedFilter != null && storedFilter instanceof CampaignFilterCommand) {
                filter = (CampaignFilterCommand) storedFilter;
            } else {
                filter = CampaignFilterCommand.getDefault();
            }
        }
        session.setAttribute(SESSION_CAMPAIGN_FILTER, filter);

        List<Campaign> campaigns = campaignService.findByFilter(filter);

        model.addAttribute("campaigns", campaigns);
        model.addAttribute("displays", displayService.findAll(false));
        model.addAttribute("filter", filter);
        return "admin/campaigns";
    }

    @GetMapping("/admin/campaigns/{id}")
    public String getCampaignForPreview(Model model, @PathVariable Long id, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:getCampaignForPreview({})", wrapper, id);

        Campaign campaign = campaignService.findById(id);

        model.addAttribute("campaign", campaign);
        model.addAttribute(campaign instanceof TextCampaign ? "textCampaign" : "pictureCampaign", campaign);
        return "admin/viewcampaign";
    }

    @GetMapping("/admin/campaigns/create/text")
    public String getTextCampaignCreate(Model model, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:getTextCampaignCreate()", wrapper);

        CampaignCommand campaign = new CampaignCommand();
        //set default values
        campaign.setType(TextCampaign.CMP_TYPE);
        campaign.setStart(LocalDate.now());
        campaign.setExpiry(LocalDate.now().plus(1, ChronoUnit.MONTHS));
        campaign.setBkgColor("#FFF");
        campaign.setTextColor("#000");
        campaign.setText("Lorem ipsum, dolor sit amet...");

        Set<Display> displays = displayService.findAll(false);

        campaign.initDisplays(displays, null);

        model.addAttribute("campaign", campaign);
        model.addAttribute("displays", displays);
        return "admin/createtextcam";
    }

    @PostMapping("/admin/campaigns/createtextprocess")
    public String processCreateTextCampaign(Model model, @ModelAttribute CampaignCommand command, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:processCreateTextCampaign({})", wrapper, command);

        if (command.getName() == null || command.getName().trim().length() == 0)
            throw new TVWordException("Name of campaign is mandatory for text campaign.", TVWordException.EC_NAME_MISSING);
        if (command.getStart() == null || command.getStart().isBefore(LocalDate.now()))
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
        Campaign campaign = campaignService.create(command.toTextCampaign());
        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CREATE_CAMPAIGN, campaign.getId());
        return "redirect:/admin/campaigns";
    }

    @GetMapping("/admin/campaigns/create/picture")
    public String getPictureCampaignCreate(Model model, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:getPictureCampaignCreate()", wrapper);

        CampaignCommand campaign = new CampaignCommand();
        //set default values
        campaign.setType(PictureCampaign.CMP_TYPE);
        campaign.setStart(LocalDate.now());
        campaign.setExpiry(LocalDate.now().plus(1, ChronoUnit.MONTHS));

        Set<Display> displays = displayService.findAll(false);

        campaign.initDisplays(displays, null);

        model.addAttribute("campaign", campaign);
        model.addAttribute("displays", displays);
        return "admin/createpicturecam";
    }

    @PostMapping("/admin/campaigns/createpicprocess")
    public String processCreatePictureCampaign(Model model, @ModelAttribute CampaignCommand command, @RequestParam("picture") MultipartFile file, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:processCreatePictureCampaign({}, {})", wrapper, command, (file != null ? file.getName() : "null"));

        if (command.getName() == null || command.getName().trim().length() == 0)
            throw new TVWordException("Name of campaign is mandatory for text campaign.", TVWordException.EC_NAME_MISSING);
        if (command.getStart() == null || command.getStart().isBefore(LocalDate.now()))
            throw new TVWordException("Invalid start date.", TVWordException.EC_INVALID_START);
        if (command.getExpiry() == null || command.getExpiry().isBefore(command.getStart()))
            throw new TVWordException("Invalid start date.", TVWordException.EC_INVALID_EXPIRY);
        if (!command.isDisplaySelected())
            throw new TVWordException("At least one display must be selected for the campaign.", TVWordException.EC_DISPLAY_NOT_ENABLED);
        if (!Image.isSupportedFormat(file.getContentType()))
            throw new TVWordException("Unsupported file content.", TVWordException.EC_UNSUPPORTED_FILE);

        Image image = new Image();
        image.setFormat(file.getContentType());
        image.setFileName(file.getName());
        try {
            image.setContent(file.getBytes());
        } catch (IOException e) {
            throw new TVWordException("Failed to read bytes of the file.", e, TVWordException.EC_UNSUPPORTED_FILE);
        }
        command.initDisplays(displayService.findAll(false));
        command.setImage(image);

        Campaign campaign = campaignService.create(command.toPictureCampaign());
        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CREATE_CAMPAIGN, campaign.getId());

        return "redirect:/admin/campaigns";
    }

    @GetMapping("/admin/campaigns/{id}/modify")
    public String getModifyCampaign(Model model, @PathVariable Long id, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:getModifyCampaign({})", wrapper, id);

        Set<Display> displays = displayService.findAll(false);

        CampaignCommand command = new CampaignCommand();
        command.populate(campaignService.findById(id), displays);

        model.addAttribute("campaign", command);
        model.addAttribute("displays", displays);

        return "admin/modifycam";
    }

    @PostMapping("/admin/campaigns/modifyprocess")
    public String processModifyCampaign(@ModelAttribute CampaignCommand command, Principal principal) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: CC:processModifyCampaign({})", wrapper, command);
        if (command.getName() == null || command.getName().trim().length() == 0)
            throw new TVWordException("Name of campaign is mandatory for text campaign.", TVWordException.EC_NAME_MISSING);
        if (command.getStart() == null)
            throw new TVWordException("Invalid start date.", TVWordException.EC_INVALID_START);
        if (command.getExpiry() == null || command.getExpiry().isBefore(command.getStart()))
            throw new TVWordException("Invalid expiry date.", TVWordException.EC_INVALID_EXPIRY);
        if (!command.isDisplaySelected())
            throw new TVWordException("At least one display must be selected for the campaign.", TVWordException.EC_DISPLAY_NOT_ENABLED);
        if (Status.parse(command.getStatus()) == null)
            throw new TVWordException("Invalid status code: " + command.getStatus(), TVWordException.EC_GENERAL_ERROR);

        command.initDisplays(displayService.findAll(false));
        campaignService.update(command);
        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_MODIFY_CAMPAIGN, command.getId());

        return "redirect:/admin/campaigns";
    }
}
