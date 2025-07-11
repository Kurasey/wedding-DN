package io.github.kaurami.wems.config;

import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.repository.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;

@Configuration
@Profile("dev")
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class TestConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

    @Bean
    ApplicationRunner runner(FamilyRepository repository, InvitationParametersHolder parametersHolder) {
        return args -> {
            Family parents = new Family("Родители", "111", "Дорогие родители", parametersHolder.getConfirmationDeadline());
            Family grandM = new Family("Бабушка", "222", "Дорогая бабушка", LocalDate.of(2025, 9, 21));
            parents.setMaxAvailableGuestCount(2);
            grandM.setMaxAvailableGuestCount(1);
            repository.save(parents);
            repository.save(grandM);
            logger.info("Initialized test data for 'dev' profile.");
        };
    }
}