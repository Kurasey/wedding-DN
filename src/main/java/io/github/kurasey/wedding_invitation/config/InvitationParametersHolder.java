package io.github.kurasey.wedding_invitation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "invitation")
public class InvitationParametersHolder {

    // Event Details
    private String groomName;
    private String brideName;
    private String eventDateString;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDateTime;
    private LocalDate confirmationDeadline;
    private String greetingText;
    private String invitationText;
    private List<String> wishesFromCouple = new ArrayList<>();

    // Venue Details
    private String venueName;
    private String venueAddress;
    private double venueLatitude;
    private double venueLongitude;
    private int mapZoom;

    // Contact Details
    private String groomPhone;
    private String groomPhoneDisplay;
    private String bridePhone;
    private String bridePhoneDisplay;
    private String telegramGroupUrl;

    // Content Snippets
    private String dressCodeText;
    private List<String> dressCodePalette = new ArrayList<>();

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public String getEventDateString() {
        return eventDateString;
    }

    public void setEventDateString(String eventDateString) {
        this.eventDateString = eventDateString;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public LocalDate getConfirmationDeadline() {
        return confirmationDeadline;
    }

    public void setConfirmationDeadline(LocalDate confirmationDeadline) {
        this.confirmationDeadline = confirmationDeadline;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }

    public String getInvitationText() {
        return invitationText;
    }

    public void setInvitationText(String invitationText) {
        this.invitationText = invitationText;
    }

    public List<String> getWishesFromCouple() {
        return wishesFromCouple;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public double getVenueLatitude() {
        return venueLatitude;
    }

    public void setVenueLatitude(double venueLatitude) {
        this.venueLatitude = venueLatitude;
    }

    public double getVenueLongitude() {
        return venueLongitude;
    }

    public void setVenueLongitude(double venueLongitude) {
        this.venueLongitude = venueLongitude;
    }

    public int getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
    }

    public String getGroomPhone() {
        return groomPhone;
    }

    public void setGroomPhone(String groomPhone) {
        this.groomPhone = groomPhone;
    }

    public String getGroomPhoneDisplay() {
        return groomPhoneDisplay;
    }

    public void setGroomPhoneDisplay(String groomPhoneDisplay) {
        this.groomPhoneDisplay = groomPhoneDisplay;
    }

    public String getBridePhone() {
        return bridePhone;
    }

    public void setBridePhone(String bridePhone) {
        this.bridePhone = bridePhone;
    }

    public String getBridePhoneDisplay() {
        return bridePhoneDisplay;
    }

    public void setBridePhoneDisplay(String bridePhoneDisplay) {
        this.bridePhoneDisplay = bridePhoneDisplay;
    }

    public String getTelegramGroupUrl() {
        return telegramGroupUrl;
    }

    public void setTelegramGroupUrl(String telegramGroupUrl) {
        this.telegramGroupUrl = telegramGroupUrl;
    }

    public String getDressCodeText() {
        return dressCodeText;
    }

    public void setDressCodeText(String dressCodeText) {
        this.dressCodeText = dressCodeText;
    }

    public List<String> getDressCodePalette() {
        return dressCodePalette;
    }

    public void setDressCodePalette(List<String> dressCodePalette) {
        this.dressCodePalette = dressCodePalette;
    }

    public void setWishesFromCouple(String wishes) {
        if (wishes != null && !wishes.isEmpty()) {
            this.wishesFromCouple = Arrays.asList(wishes.split("\\s*;\\s*"));
        }
    }
}
