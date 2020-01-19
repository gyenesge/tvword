package home.gabe.tvword.controllers;

import home.gabe.tvword.idokep.IdokepDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class IdokepController {

    private IdokepDownloader idokep;

    public IdokepController(IdokepDownloader idokep) {
        this.idokep = idokep;
    }

    @GetMapping("/idokep/list")
    public String getImgList(Model model) {
        log.info("Idokep: get image list + manual download");
        List<String> imageUrls = idokep.getImageUrls();
        model.addAttribute("imageUrls", imageUrls);
        model.addAttribute("filenames", idokep.downloadPictures(imageUrls, "Manual"));
        return "idokep/imglist";
    }

    @GetMapping("/idokep/status")
    public String getStatus(Model model) {
        log.info("Idokep: get status");
        List<String> logs = idokep.getLogList();
        List<String> reverseLogs = new ArrayList<>(logs);
        Collections.reverse(reverseLogs);
        model.addAttribute("logs", reverseLogs);
        return "idokep/status";
    }
}
