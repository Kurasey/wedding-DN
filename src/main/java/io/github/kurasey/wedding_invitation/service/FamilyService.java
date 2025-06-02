package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.repository.FamilyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final GuestService guestService;
    private final FamilyCodeGenerator codeGenerator;

    public FamilyService(FamilyRepository familyRepository, GuestService guestService, FamilyCodeGenerator codeGenerator) {
        this.familyRepository = familyRepository;
        this.guestService = guestService;
        this.codeGenerator = codeGenerator;
    }

    @Transactional
    public Family createFamily(String name ,String appeal, LocalDateTime confirmationDeadline) {
        return familyRepository.save(new Family(name, codeGenerator.nextUniqueCode(), appeal, confirmationDeadline));
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
    public Family updateFamily(Long familyId, Family familyDetails) {
        Family family = getFamilyById(familyId);
        family.setPersonalLink(familyDetails.getPersonalLink());
        family.setAppeal(familyDetails.getAppeal());
        family.setPhone(familyDetails.getPhone());
        family.setTransferRequired(familyDetails.isTransferRequired());
        family.setPlacementRequired(familyDetails.isPlacementRequired());
        family.setActive(familyDetails.isActive());
        return familyRepository.save(family);
    }

    @Transactional
    public void deleteFamily(Long id){
        Family family = getFamilyById(id);
        familyRepository.delete(family);
    }

    @Transactional
    public Family addGuestToFamily(Long familyId, Guest guest){
        Family family = getFamilyById(familyId);
        guest = guestService.save(guest);
        if (family.getGuests() == null) {
            family.setGuests(new ArrayList<>());
        }
        family.getGuests().add(guest);
        return familyRepository.save(family);
    }

    @Transactional
    public Family removeGuestFromFamily(Long familyId, Long guestId) {
        Family family = getFamilyById(familyId);
        if (family.getGuests() != null) {
            family.getGuests().removeIf(guest -> guest.getId().equals(guestId));
        }
        return familyRepository.save(family);
    }

    public List<Guest> getFamilyGuests(Long familyId) {
        Family family = getFamilyById(familyId);
        return family.getGuests() != null ? family.getGuests() : Collections.emptyList();
    }

    @Transactional
    public Family updateTransferRequirement(Long id, boolean isRequired) {
        Family family = getFamilyById(id);
        family.setTransferRequired(isRequired);
        return familyRepository.save(family);
    }

    @Transactional
    public Family updatePlacementRequirement(Long id, boolean isRequired) {
        Family family = getFamilyById(id);
        family.setPlacementRequired(isRequired);
        return familyRepository.save(family);
    }
}
