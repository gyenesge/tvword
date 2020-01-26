package home.gabe.tvword.controllers;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.web.AuditFilterCommand;
import home.gabe.tvword.services.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class AuditController {
    private AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/admin/auditLog")
    public String getAuditLogs(Principal principal, Model model, @ModelAttribute AuditFilterCommand filter) {
        UserPrincipalWrapper wrapper = new UserPrincipalWrapper(principal);
        log.info("{}: AuC:getAuditLogs( {} )", wrapper, filter);

        if (filter == null || filter.getUserFilter() == null)
            filter = AuditFilterCommand.getDefault();

        List<AuditEvent> events = auditService.getEvents(filter);
        List<User> users = auditService.getAllUsers();
        model.addAttribute("events", events);
        model.addAttribute("users", users);
        model.addAttribute("filter", filter);
        return "admin/auditlog";
    }

}
