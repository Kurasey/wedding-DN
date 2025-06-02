package io.github.kurasey.wedding_invitation.repository;

import io.github.kurasey.wedding_invitation.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findByPersonalLink(String personalLink);
}