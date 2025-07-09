package io.github.kurasey.wedding_invitation.model;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @NotBlank(message = "Название семьи не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String name;

    @Column(unique = true)
    private String personalLink;

    @NotBlank(message = "Обращение не может быть пустым")
    @Size(max = 500, message = "Обращение не должно превышать 500 символов")
    private String appeal;

    private String phone;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_transfer_required")
    private boolean transferRequired;

    @Column(name = "is_placement_required")
    private boolean placementRequired;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name ASC")
    private List<Guest> guests = new ArrayList<>();

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitHistoryRecord> visitHistory = new ArrayList<>();

    @NotNull(message = "Дедлайн подтверждения обязателен")
    @Future(message = "Дедлайн подтверждения должен быть в будущем")
    private LocalDate confirmationDeadline;

    @Min(value = 1, message = "Минимум 1 гость")
    private int maxAvailableGuestCount;

    public boolean isExpiredDeadline() {
        return confirmationDeadline != null && LocalDate.now().isAfter(confirmationDeadline);
    }

    public Family(String name, String personalLink, String appeal, LocalDate confirmationDeadline) {
        this.name = name;
        this.personalLink = personalLink;
        this.appeal = appeal;
        this.confirmationDeadline = confirmationDeadline;
        this.active = true;
        this.maxAvailableGuestCount = 2; // Значение по-умолчанию
    }

    public Family() {
        this.active = true;
        this.guests = new ArrayList<>();
        this.maxAvailableGuestCount = 2; // Значение по-умолчанию
    }

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPersonalLink() { return personalLink; }
    public void setPersonalLink(String personalLink) { this.personalLink = personalLink; }
    public String getAppeal() { return appeal; }
    public void setAppeal(String appeal) { this.appeal = appeal; }
    public String getPhone() { return phone; }
/*    public void setPhone(String phone) { this.phone = phone; }*/
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isTransferRequired() { return transferRequired; }
    public void setTransferRequired(boolean transferRequired) { this.transferRequired = transferRequired; }
    public boolean isPlacementRequired() { return placementRequired; }
    public void setPlacementRequired(boolean placementRequired) { this.placementRequired = placementRequired; }
    public List<Guest> getGuests() { return guests; }
    public void setGuests(List<Guest> guests) { this.guests = guests; }
    public LocalDate getConfirmationDeadline() { return confirmationDeadline; }
    public void setConfirmationDeadline(LocalDate confirmationDeadline) { this.confirmationDeadline = confirmationDeadline; }
    public int getMaxAvailableGuestCount() { return maxAvailableGuestCount; }
    public void setMaxAvailableGuestCount(int maxAvailableGuestCount) { this.maxAvailableGuestCount = maxAvailableGuestCount; }
    public List<VisitHistoryRecord> getVisitHistory() {
        return visitHistory;
    }
    public void setVisitHistory(List<VisitHistoryRecord> visitHistory) {
        this.visitHistory = visitHistory;
    }

    public void setPhone(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            this.phone = null;
            return;
        }
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneUtil.parse(rawPhone, "RU");
            if (phoneUtil.isValidNumber(number)) {
                this.phone = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
            } else {
                this.phone = rawPhone; // Сохраняем как есть, если невалидный
            }
        } catch (NumberParseException e) {
            this.phone = rawPhone;
        }
    }

    /**
     * Возвращает номер телефона, отформатированный для отображения.
     * Не маппится на колонку в БД (благодаря аннотации @Transient).
     * @return Красиво отформатированный номер или пустая строка.
     */
    @Transient
    public String getDisplayPhone() {
        if (this.phone == null || this.phone.isBlank()) {
            return "";
        }
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneUtil.parse(this.phone, "RU");
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            // Если в БД по какой-то причине оказался невалидный номер,
            // просто вернем его как есть.
            return this.phone;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Family family = (Family) o;
        return id != null && Objects.equals(id, family.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}