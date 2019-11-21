package home.gabe.tvword.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    private String firstName;
    private String lastName;

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }
}
