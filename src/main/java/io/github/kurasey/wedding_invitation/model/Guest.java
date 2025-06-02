package io.github.kurasey.wedding_invitation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    private Family family;

    @NotNull
    private String name;

    @NotNull
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private EnumSet<Beverage> beverages;

    private boolean willAttend;

    public Guest(Family family, String name, EnumSet<Beverage> beverages) {
        this.family = family;
        this.name = name;
        this.beverages = beverages;
        this.willAttend = true;
    }

    private Guest() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumSet<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(EnumSet<Beverage> beverages) {
        this.beverages = beverages;
    }

    public boolean isWillAttend() {
        return willAttend;
    }

    public void setWillAttend(boolean willAttend) {
        this.willAttend = willAttend;
    }
}
