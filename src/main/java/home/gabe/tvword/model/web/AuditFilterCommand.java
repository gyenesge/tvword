package home.gabe.tvword.model.web;

import home.gabe.tvword.model.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class AuditFilterCommand {

    public final static Long ALL_USER = -1L;
    public final static String ALL_ROLE = "all";

    private String roleFilter;
    private Long userFilter;
    private LocalDate from;
    private LocalDate to;

    public static AuditFilterCommand getDefault() {
        AuditFilterCommand command = new AuditFilterCommand();
        command.roleFilter = UserRole.ADMIN.getId();
        command.userFilter = ALL_USER;

        LocalDate now = LocalDate.now();
        command.from = now.minus(1, ChronoUnit.WEEKS);
        command.to = now;

        return command;
    }

    @Override
    public String toString() {
        return "AuditFilterCommand{" +
                "roleFilter='" + roleFilter + '\'' +
                ", userFilter=" + userFilter +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
