package home.gabe.tvword.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Status {
    public final static Status ACTIVE = new Status("A", "Active");
    public final static Status DELETED = new Status("D", "Deleted");

    @Id
    private String statusCode;

    private String description;

    public Status() {
    }

    public Status(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public static Status parse(String status) {
        if (status.equals(ACTIVE.statusCode))
            return ACTIVE;
        if (status.equals(DELETED.statusCode))
            return DELETED;

        throw new IllegalArgumentException("Unknown status code: " + status);
    }

    public boolean isActive() {
        return ACTIVE.statusCode.equals(statusCode);
    }

    public boolean isDeleted() {
        return DELETED.statusCode.equals(statusCode);
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusCode='" + statusCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
