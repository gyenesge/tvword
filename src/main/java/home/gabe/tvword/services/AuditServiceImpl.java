package home.gabe.tvword.services;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.UserRole;
import home.gabe.tvword.model.web.AuditFilterCommand;
import home.gabe.tvword.repositories.AuditRepository;
import home.gabe.tvword.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AuditServiceImpl implements ApplicationListener<AbstractAuthenticationEvent>, AuditService {

    private AuditRepository auditRepository;
    private UserRepository userRepository;

    private boolean logInteractiveAuthenticationSuccessEvents = true;

    public AuditServiceImpl(AuditRepository auditRepository, UserRepository userRepository) {
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AuditEvent logEvent(String user, String event) {
        return logEvent(user, event, null);
    }

    @Override
    public AuditEvent logEvent(String user, String event, Long objectId) {
        AuditEvent eventObject = new AuditEvent(user, event, objectId);
        auditRepository.save(eventObject);
        return eventObject;
    }

    @Override
    public List<AuditEvent> getEvents(AuditFilterCommand filter) {
        List<AuditEvent> events = null;

        if (!filter.getUserFilter().equals(AuditFilterCommand.ALL_USER)) {
            Optional<User> optional = userRepository.findById(filter.getUserFilter());
            if (optional.isEmpty() || (!filter.getRoleFilter().equals(AuditFilterCommand.ALL_ROLE) && !optional.get().getRole().getId().equals(filter.getRoleFilter()))) {
                return new ArrayList<>();
            }
            User user = optional.get();
            events = auditRepository.findAllByUserAndDate(user, filter.getFrom().atStartOfDay(), filter.getTo().atTime(23, 59));
        } else if (!filter.getRoleFilter().equals(AuditFilterCommand.ALL_ROLE)) {
            UserRole role = UserRole.findRoleById(filter.getRoleFilter());
            events = auditRepository.findAllByRoleAndDate(role, filter.getFrom().atStartOfDay(), filter.getTo().atTime(23, 59));
        } else {
            events = auditRepository.findAllByDate(filter.getFrom().atStartOfDay(), filter.getTo().atTime(23, 59));
        }

        return events;
    }

    @Override
    public List<User> getAllUsers() {
        Iterable<User> all = userRepository.findAll();
        List<User> users = new ArrayList<>();
        all.forEach(user -> users.add(user));

        Collections.sort(users);

        return users;
    }

    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (this.logInteractiveAuthenticationSuccessEvents || !(event instanceof InteractiveAuthenticationSuccessEvent)) {
            if (log.isWarnEnabled()) {
                StringBuilder builder = new StringBuilder();
                builder.append("AUDIT: ");
                builder.append(ClassUtils.getShortName(event.getClass()));
                builder.append(": ");
                builder.append(event.getAuthentication().getName());
                builder.append("; details: ");
                builder.append(event.getAuthentication().getDetails());
                if (event instanceof AbstractAuthenticationFailureEvent) {
                    builder.append("; exception: ");
                    builder.append(((AbstractAuthenticationFailureEvent) event).getException().getMessage());
                }

                log.warn(builder.toString());

                if (event instanceof AuthenticationSuccessEvent)
                    logEvent(event.getAuthentication().getName(), AuditEvent.AE_LOGIN);
                else if (event instanceof LogoutSuccessEvent)
                    logEvent(event.getAuthentication().getName(), AuditEvent.AE_LOGOUT);
                else if (event instanceof AbstractAuthenticationFailureEvent)
                    logEvent(event.getAuthentication().getName(), AuditEvent.AE_FAILED_LOGIN);
            }
        }
    }

    public boolean isLogInteractiveAuthenticationSuccessEvents() {
        return this.logInteractiveAuthenticationSuccessEvents;
    }

    public void setLogInteractiveAuthenticationSuccessEvents(boolean logInteractiveAuthenticationSuccessEvents) {
        this.logInteractiveAuthenticationSuccessEvents = logInteractiveAuthenticationSuccessEvents;
    }
}

