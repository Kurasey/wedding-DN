package io.github.kurasey.wedding_invitation.config;

import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile("dev")
public class TestConfig {

    @Bean
    ApplicationRunner runner(FamilyRepository repository) {
        return args -> {
            repository.save(new Family("Родители","111", "Дорогие родители", LocalDateTime.of(2025,05,21,15,32)));
            repository.save(new Family( "Бабушка", "222","Дорогая бабушка>", LocalDateTime.of(2025,05,21,15,32)));
        };
    }
}
