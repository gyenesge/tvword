package home.gabe.tvword.controllers;

import home.gabe.tvword.idokep.IdokepDownloader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IdokepController {

    private IdokepDownloader idokep;

    public IdokepController(IdokepDownloader idokep) {
        this.idokep = idokep;
    }

    @GetMapping("/idokep/list")
    public String getImgList(Model model) {
        List<String> imageUrls = idokep.getImageUrls();
        model.addAttribute("imageUrls", imageUrls);
        model.addAttribute("filenames", idokep.downloadPictures(imageUrls));
        return "/idokep/imglist";
    }

    @GetMapping("/idokep/status")
    public String getStatus(Model model) {
        model.addAttribute("lastRun", idokep.getLastRun());
        return "/idokep/status";
    }
}
