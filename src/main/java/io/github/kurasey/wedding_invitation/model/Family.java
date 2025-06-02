package io.github.kurasey.wedding_invitation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @NotNull
    private String name;

    @NotNull
    private String personalLink;

    @NotNull
    private String appeal;

    private String phone;

    private boolean isActive;

    private boolean isTransferRequired;

    private boolean isPlacementRequired;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guest> guests = new ArrayList<>();

    @NotNull
    private LocalDateTime confirmationDeadline;

    public Family(String name, String personalLink, String appeal, LocalDateTime confirmationDeadline) {
        this.name = name;
        this.personalLink = personalLink;
        this.appeal = appeal;
        this.confirmationDeadline = confirmationDeadline;
    }

    private Family() {
    }

    public Long getId() {
        return id;
    }

    public String getPersonalLink() {
        return personalLink;
    }

    public void setPersonalLink(String personalLink) {
        this.personalLink = personalLink;
    }

    public String getAppeal() {
        return appeal;
    }

    public void setAppeal(String appeal) {
        this.appeal = appeal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isTransferRequired() {
        return isTransferRequired;
    }

    public void setTransferRequired(boolean transferRequired) {
        isTransferRequired = transferRequired;
    }

    public boolean isPlacementRequired() {
        return isPlacementRequired;
    }

    public void setPlacementRequired(boolean placementRequired) {
        isPlacementRequired = placementRequired;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getConfirmationDeadline() {
        return confirmationDeadline;
    }

    public void setConfirmationDeadline(LocalDateTime confirmationDeadline) {
        this.confirmationDeadline = confirmationDeadline;
    }

    public String getName() {
        return name;
    }


}
