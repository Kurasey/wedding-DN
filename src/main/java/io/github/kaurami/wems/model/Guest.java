package io.github.kaurami.wems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @NotBlank(message = "Имя гостя не может быть пустым")
    private String name;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "guest_beverages", joinColumns = @JoinColumn(name = "guest_id"))
    @Column(name = "beverage")
    private Set<Beverage> beverages = new HashSet<>();

    private boolean willAttend;

    public Guest(Family family, String name, Set<Beverage> beverages) {
        this.family = family;
        this.name = name;
        this.beverages = beverages;
    }

    public Guest() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Family getFamily() { return family; }
    public void setFamily(Family family) { this.family = family; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Beverage> getBeverages() { return beverages; }
    public void setBeverages(Set<Beverage> beverages) { this.beverages = beverages; }
    public boolean isWillAttend() { return willAttend; }
    public void setWillAttend(boolean willAttend) { this.willAttend = willAttend; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return id != null && Objects.equals(id, guest.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}