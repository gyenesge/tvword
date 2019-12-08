package home.gabe.tvword.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @RequestMapping("/admin/displays")
    public String getDisplayIndex() {
        return "/admin/displays";
    }

    @RequestMapping("/admin/displays/create")
    public String getCreateDisplay() {
        return "/admin/createdisplay";
    }

    @RequestMapping("/admin/displays/modify")
    public String getModifyDisplay() {
        return "/admin/modifydisplay";
    }

    @RequestMapping("/admin/campaigns")
    public String getCampaignIndex() {
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

    @RequestMapping("/admin/campaigns/modify")
    public String getModifyCampaign() {
        return "/admin/modifycam";
    }
}
