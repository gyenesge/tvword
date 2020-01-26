package home.gabe.tvword.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class AuditEvent implements Comparable<AuditEvent> {

    public static final String AE_LOGIN = "Login";
    public static final String AE_LOGOUT = "Logout";
    public static final String AE_FAILED_LOGIN = "Login failed";

    public static final String AE_CHANGE_PASSWORD = "Change password";

    public static final String AE_CREATE_CAMPAIGN = "Create campaign";
    public static final String AE_MODIFY_CAMPAIGN = "Modify campaign";
    public static final String AE_EXECUTE_CAMPAIGN = "Execute campaign";

    public static final String AE_CREATE_DISPLAY = "Create new display";
    public static final String AE_MODIFY_DISPLAY = "Modify display";
    public static final String AE_CONFIG_PAGE = "Display config page";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String event;

    /**
     * Object Id can hold any ID that is subject of the event. E.g. campaign Id, if a campaign is display,
     * or display ID, if a new display is created or modified.
     */
    @Column(nullable = true)
    private Long objectId;

    public AuditEvent() {
    }

    public AuditEvent(String user, String event) {
        this.user = user;
        this.event = event;
    }

    public AuditEvent(String user, String event, Long objectId) {
        this.user = user;
        this.event = event;
        this.objectId = objectId;
    }


    @Override
    public int compareTo(AuditEvent auditEvent) {
        return -timestamp.compareTo(auditEvent.timestamp);
    }
}
