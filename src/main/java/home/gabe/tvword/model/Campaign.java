package home.gabe.tvword.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private LocalDateTime start;
    private LocalDateTime expiry;

    @ManyToMany(mappedBy = "campaigns")
    private Set<Display> displays = new HashSet<>();

    @ManyToOne
    private Status status = Status.ACTIVE;

    public Campaign() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    public Set<Display> getDisplays() {
        return displays;
    }

    public void setDisplays(Set<Display> displays) {
        this.displays = displays;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

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
