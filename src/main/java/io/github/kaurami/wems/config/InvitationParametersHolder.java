package io.github.kaurami.wems.config;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "invitation")
public class InvitationParametersHolder {

    private static final Logger logger = LoggerFactory.getLogger(InvitationParametersHolder.class);
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    // Event Details
    private String groomName;
    private String brideName;

    private String eventTimeZone;
    private LocalDateTime eventDateTime;
    private ZonedDateTime actualEventZonedDateTimeWithTimeZone;
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
    private String bridePhone;
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

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public ZonedDateTime getEventDateTime() {
        return actualEventZonedDateTimeWithTimeZone;
    }

    @PostConstruct
    public void initEventDateTime() {
        if (this.eventTimeZone == null || this.eventTimeZone.isBlank()) {
            ZoneId fallbackZone = ZoneId.systemDefault();
            logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            logger.warn("!!! Timezone (`invitation.eventTimeZone`) is not configured. ");
            logger.warn("!!! Falling back to server's default timezone: {}", fallbackZone);
            logger.warn("!!! This can lead to incorrect countdown timers if the server is not in the event's timezone.");
            logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            this.actualEventZonedDateTimeWithTimeZone = this.eventDateTime.atZone(fallbackZone);
        } else {
            try {
                this.actualEventZonedDateTimeWithTimeZone = this.eventDateTime.atZone(ZoneId.of(this.eventTimeZone));
                logger.info("Successfully configured event time in timezone: {}", this.eventTimeZone);
            } catch (Exception e) {
                logger.error("!!! Invalid timezone configured: '{}'. Falling back to system default.", this.eventTimeZone, e);
                this.actualEventZonedDateTimeWithTimeZone = this.eventDateTime.atZone(ZoneId.systemDefault());
            }
        }
    }

    public String getEventTimeZone() {
        return eventTimeZone;
    }

    public void setEventTimeZone(String eventTimeZone) {
        this.eventTimeZone = eventTimeZone;
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
        return formatPhoneNumber(groomPhone);
    }

    public String getBridePhone() {
        return bridePhone;
    }

    public void setBridePhone(String bridePhone) {
        this.bridePhone = bridePhone;
    }

    public String getBridePhoneDisplay() {
        return formatPhoneNumber(bridePhone);
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

    public String getGroomPhoneHref() {
        return formatAsHref(groomPhone);
    }

    public String getBridePhoneHref() {
        return formatAsHref(this.bridePhone);
    }

    /**
     * Форматирует номер в международный стандарт для отображения пользователю.
     * Например, "89281234567" -> "+7 928 123-45-67"
     * @param rawPhone исходная строка с номером
     * @return отформатированная строка или исходная, если парсинг не удался
     */
    private String formatPhoneNumber(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return "";
        }
        try {
            // "RU" - это подсказка для библиотеки, из какой страны номер, если он не в международном формате (например, без +7)
            Phonenumber.PhoneNumber number = phoneUtil.parse(rawPhone, "RU");
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            logger.warn("Could not parse phone number '{}'. Returning as is. Reason: {}", rawPhone, e.getMessage());
            return rawPhone; // Если не смогли распознать, возвращаем как есть
        }
    }

    /**
     * Форматирует номер в стандарт RFC3966 для ссылок tel:
     * Например, "8 (928) 123-45-67" -> "tel:+79281234567"
     * @param rawPhone исходная строка с номером
     * @return строка для href или пустая строка, если парсинг не удался
     */
    private String formatAsHref(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return "";
        }
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(rawPhone, "RU");
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966); // -> tel:+...
        } catch (NumberParseException e) {
            return "tel:" + rawPhone.replaceAll("\\D", ""); // Запасной вариант - просто оставить цифры
        }
    }

}
