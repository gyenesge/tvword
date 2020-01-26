package home.gabe.tvword.repositories;

import home.gabe.tvword.model.AuditEvent;
import home.gabe.tvword.model.User;
import home.gabe.tvword.model.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditRepository extends CrudRepository<AuditEvent, Long> {

    @Query(value = "SELECT ae FROM AuditEvent ae, User u WHERE ae.user=u.name and u.role=:role and ae.timestamp >= :from and ae.timestamp <= :to ORDER BY ae.timestamp DESC")
    List<AuditEvent> findAllByRoleAndDate(UserRole role, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT ae FROM AuditEvent ae, User u WHERE ae.user=u.name and u=:user and ae.timestamp >= :from and ae.timestamp <= :to ORDER BY ae.timestamp DESC")
    List<AuditEvent> findAllByUserAndDate(User user, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT ae FROM AuditEvent ae WHERE ae.timestamp >= :from and ae.timestamp <= :to ORDER BY ae.timestamp DESC")
    List<AuditEvent> findAllByDate(LocalDateTime from, LocalDateTime to);
}
