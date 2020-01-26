package home.gabe.tvword.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CampaignType type;

    @Column(unique = true)
    private String name;
    private LocalDate start;
    private LocalDate expiry;

    @ManyToMany(mappedBy = "campaigns")
    private Set<Display> displays = new HashSet<>();

    @ManyToOne
    private Status status = Status.ACTIVE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return Objects.equals(id, campaign.id) &&
                Objects.equals(name, campaign.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Campaign{" +
                toStringSuper() +
                '}';
    }

    public String toStringSuper() {
        return "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", expiry=" + expiry +
                ", status=" + status;
    }
}
