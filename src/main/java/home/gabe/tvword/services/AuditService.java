package home.gabe.tvword.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Slf4j
@Service
public class AuditService implements ApplicationListener<AbstractAuthenticationEvent> {
    private boolean logInteractiveAuthenticationSuccessEvents = true;

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

