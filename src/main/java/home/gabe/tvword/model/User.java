package home.gabe.tvword.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    protected Long id;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    protected String name;

    protected String hashedPassword;

    @Transient
    protected String password;

    @ManyToOne
    protected UserRole role;

    @ManyToOne
    protected Status status = Status.ACTIVE;


}
