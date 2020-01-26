package home.gabe.tvword.controllers;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.web.PasswordCommand;
import home.gabe.tvword.services.AdminService;
import home.gabe.tvword.services.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@Controller
public class AdminController {

    private AdminService adminService;
    private AuditService auditService;

    public AdminController(AdminService adminService, AuditService auditService) {
        this.adminService = adminService;
        this.auditService = auditService;
    }

    @GetMapping("/admin/changepwd")
    public String getChangePasswordForm(Principal principal, Model model, @RequestParam(name = "success", required = false, defaultValue = "false") String success) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: AC: getChangePasswordForm(success={})", wrapper, success);

        Boolean bool = Boolean.parseBoolean(success);

        model.addAttribute("pwdCommand", new PasswordCommand());
        model.addAttribute("success", bool);

        return "admin/changepwd";
    }

    @PostMapping("/admin/changepwd_process")
    public String processChangePassword(Principal principal, @ModelAttribute("pwdCommand") PasswordCommand command) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: AC: processChangePassword()", wrapper); //not logging password fields

        adminService.changePassword(wrapper.getUser(), command);
        auditService.logEvent(wrapper.getUser().getName(), AuditEvent.AE_CHANGE_PASSWORD);

        return "redirect:/admin/changepwd?success=true";
    }

}
