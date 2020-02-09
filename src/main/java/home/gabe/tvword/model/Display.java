package home.gabe.tvword.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Display extends User {

    /**
     * Arbitrary comment, that can contain location info, type of device, instructions for the admin...
     */
    private String note;
    /**
     * Time period while display waits between switching campaigns.
     */
    private Integer refreshTime;

    /**
     * List of campaigns assigned to this display.
     */
    @ManyToMany
    @JoinTable(
            name = "display_campaign",
            joinColumns = @JoinColumn(name = "display_id"),
            inverseJoinColumns = @JoinColumn(name = "campaign_id")
    )
    private Set<Campaign> campaigns = new HashSet<>();

    public Display() {
        super();
        setRole(UserRole.DISPLAY);
    }
}
