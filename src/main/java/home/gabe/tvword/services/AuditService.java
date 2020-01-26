package home.gabe.tvword.services;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.web.AuditFilterCommand;

import java.util.List;

public interface AuditService {
    AuditEvent logEvent(String user, String event);

    AuditEvent logEvent(String user, String event, Long objectId);

    List<AuditEvent> getEvents(AuditFilterCommand filter);

    List<User> getAllUsers();
}
