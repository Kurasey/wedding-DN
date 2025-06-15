package io.github.kurasey.wedding_invitation.config;

import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("dev")
public class TestConfig {

    @Bean
    ApplicationRunner runner(FamilyRepository repository, InvitationParametersHolder parametersHolder) {
        return args -> {
            Family parents = new Family("Родители", "111", "Дорогие родители", parametersHolder.getConfirmationDeadline());
            Family grandM = new Family("Бабушка", "222", "Дорогая бабушка", LocalDate.of(2025, 9, 21));
            parents.setMaxAvailableGuestCount(2);
            grandM.setMaxAvailableGuestCount(1);
            repository.save(parents);
            repository.save(grandM);
            System.err.println("Test profile");
        };
    }
}
