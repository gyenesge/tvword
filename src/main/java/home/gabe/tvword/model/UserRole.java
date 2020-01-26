package home.gabe.tvword.model;

import home.gabe.tvword.controllers.TVWordException;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class UserRole {
    public final static String SPRING_ROLE_NAME_PREFIX = "ROLE_";
    public final static UserRole DISPLAY = new UserRole("DISPLAY", "Diplay user who can display campaigns");
    public final static UserRole ADMIN = new UserRole("ADMIN", "Admin user, who can manage displays and campaigns.");

    @Id
    private String id;
    private String description;

    public UserRole() {
    }

    public UserRole(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static UserRole findRoleById(String roleFilter) {
        if (DISPLAY.id.equals(roleFilter))
            return DISPLAY;
        else if (ADMIN.id.equals(roleFilter))
            return ADMIN;

        throw new TVWordException("Invalid role id: " + roleFilter, TVWordException.EC_GENERAL_ERROR);
    }

    public String getSpringRoleName() {
        if (id.startsWith(SPRING_ROLE_NAME_PREFIX))
            return id;
        return SPRING_ROLE_NAME_PREFIX + id;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
