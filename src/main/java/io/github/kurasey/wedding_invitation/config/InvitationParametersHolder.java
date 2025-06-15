package io.github.kurasey.wedding_invitation.config;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvitationParametersHolder {

    LocalDate confirmationDeadline = LocalDate.of(2025,8,1);

    public LocalDate getConfirmationDeadline() {
        return confirmationDeadline;
    }

    public void setConfirmationDeadline(LocalDate confirmationDeadline) {
        this.confirmationDeadline = confirmationDeadline;
    }
}
