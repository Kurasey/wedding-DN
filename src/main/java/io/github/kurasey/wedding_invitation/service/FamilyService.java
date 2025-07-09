package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.repository.FamilyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyCodeGenerator codeGenerator;

    public FamilyService(FamilyRepository familyRepository, FamilyCodeGenerator codeGenerator) {
        this.familyRepository = familyRepository;
        this.codeGenerator = codeGenerator;
    }

    public Family getFamilyById(Long id) {
        return familyRepository.findById(id).orElseThrow(() -> new NotFoundFamily("Не найдена семья с ID " + id));
    }

    public Family getByPersonalLink(String personalLink){
        return familyRepository.findByPersonalLink(personalLink).orElseThrow(()->new NotFoundFamily("Не найдена семья с ссылкой " + personalLink));
    }

    public List<Family> getAllFamilies() {
        return familyRepository.findAll();
    }

    @Transactional
    public Family createFamily(Family family) {
        if (family.getPersonalLink() == null || family.getPersonalLink().isBlank()) {
            family.setPersonalLink(codeGenerator.nextUniqueCode());
        }
        if (family.getGuests() == null) {
            family.setGuests(new ArrayList<>());
        }
        return familyRepository.save(family);
    }

    @Transactional
    public Family updateFamily(Long familyId, Family familyDetails) {
        Family family = getFamilyById(familyId);
        family.setName(familyDetails.getName());
        family.setAppeal(familyDetails.getAppeal());
        family.setPhone(familyDetails.getPhone());
        family.setTransferRequired(familyDetails.isTransferRequired());
        family.setPlacementRequired(familyDetails.isPlacementRequired());
        family.setActive(familyDetails.isActive());
        family.setConfirmationDeadline(familyDetails.getConfirmationDeadline());
        family.setMaxAvailableGuestCount(familyDetails.getMaxAvailableGuestCount());
        return familyRepository.save(family);
    }

//    Использовать только для существующих семей
    @Transactional
    public Family persist(Family family) {
        return familyRepository.save(family);
    }

    @Transactional
    public void deleteFamily(Long id){
        if (familyRepository.existsById(id)) {
            familyRepository.deleteById(id);
        } else {
            throw new NotFoundFamily("Не найдена семья с ID " + id + " для удаления");
        }
    }

    @Transactional
    public void addGuestToFamily(Long familyId, Guest guest) {
        Family family = getFamilyById(familyId);
        guest.setFamily(family);
        family.getGuests().add(guest);
        familyRepository.save(family);
    }

    @Transactional
    public void removeGuestFromFamily(Long familyId, Long guestId) {
        Family family = getFamilyById(familyId);
        family.getGuests().removeIf(guest -> guest.getId().equals(guestId));
        familyRepository.save(family);
    }

    public List<Guest> getFamilyGuests(Long familyId) {
        Family family = getFamilyById(familyId);
        return family.getGuests() != null ? family.getGuests() : Collections.emptyList();
    }

    public List<Family> findProblemFamilies(LocalDate date) {
        return familyRepository.findProblemFamilies(date);
    }

    public List<Family> findFamiliesWithNoConfirmedGuests() {
        return familyRepository.findFamiliesWithNoConfirmedGuests();
    }
}