package io.github.kaurami.wems.service;

import io.github.kaurami.wems.exception.NotFoundGuestException;
import io.github.kaurami.wems.model.Beverage;
import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.model.Guest;
import io.github.kaurami.wems.repository.FamilyRepository;
import io.github.kaurami.wems.repository.GuestRepository;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final FamilyRepository familyRepository;

    public GuestService(GuestRepository guestRepository, FamilyRepository familyRepository) {
        this.guestRepository = guestRepository;
        this.familyRepository = familyRepository;
    }

    public List<Guest> findAll(){
        return guestRepository.findAll();
    }

    public List<Guest> findAllOrderByFamily() {
        return guestRepository.findAll(Sort.by("family.name", "name"));
    }

    public Optional<Guest> findById(Long id) {
        return guestRepository.findById(id);
    }

    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }

    public void deleteById(Long id) {
        guestRepository.deleteById(id);
    }

    public List<Guest> findGuestByBeverage(Beverage beverage) {
        return guestRepository.findByBeveragesContaining(beverage);
    }

    public List<Guest> findWithFilters(Boolean attending, Boolean transfer, Boolean placement) {
        Specification<Guest> spec = Specification.where(null);

        if (attending != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("willAttend"), attending));
        }
        if (transfer != null) {
            // Создаем join с семьей для фильтрации
            spec = spec.and((root, query, cb) -> {
                Join<Guest, Family> familyJoin = root.join("family");
                return cb.equal(familyJoin.get("transferRequired"), transfer);
            });
        }
        if (placement != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Guest, Family> familyJoin = root.join("family");
                return cb.equal(familyJoin.get("placementRequired"), placement);
            });
        }
        return guestRepository.findAll(spec);
    }

    public Map<String, Object> getDashboardStats() {
        List<Guest> confirmedGuests = guestRepository.findAllConfirmedWithFamily();

        List<Guest> guestsNeedingTransfer = confirmedGuests.stream()
                .filter(g -> g.getFamily().isTransferRequired())
                .toList();
        long familiesNeedingTransfer = guestsNeedingTransfer.stream().map(Guest::getFamily).distinct().count();
        long peopleNeedingTransfer = guestsNeedingTransfer.size();

        List<Guest> guestsNeedingPlacement = confirmedGuests.stream()
                .filter(g -> g.getFamily().isPlacementRequired())
                .toList();
        long familiesNeedingPlacement = guestsNeedingPlacement.stream().map(Guest::getFamily).distinct().count();
        long peopleNeedingPlacement = guestsNeedingPlacement.size();

        Map<Beverage, Long> beverageStats = confirmedGuests.stream()
                .flatMap(guest -> guest.getBeverages().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long totalGuests = guestRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGuests", totalGuests);
        stats.put("confirmedGuests", (long) confirmedGuests.size());
        stats.put("beverageStats", beverageStats);
        stats.put("allBeverages", EnumSet.allOf(Beverage.class));

        stats.put("familiesNeedingTransfer", familiesNeedingTransfer);
        stats.put("peopleNeedingTransfer", peopleNeedingTransfer);
        stats.put("familiesNeedingPlacement", familiesNeedingPlacement);
        stats.put("peopleNeedingPlacement", peopleNeedingPlacement);

        return stats;
    }


    @Transactional
    public Guest updateGuest(Long guestId, Guest guestDetails) {
        Guest existingGuest = findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден."));

        existingGuest.setName(guestDetails.getName());
        existingGuest.setWillAttend(guestDetails.isWillAttend());
        existingGuest.setBeverages(guestDetails.getBeverages());

        return save(existingGuest);
    }

    @Transactional
    public Guest addNewGuestToFamily(Long familyId, Guest newGuest) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalStateException("Семья не найдена"));

        if (family.getGuests().size() >= family.getMaxAvailableGuestCount()) {
            throw new IllegalStateException("Достигнуто максимальное количество гостей для этой семьи.");
        }

        newGuest.setFamily(family);
        return save(newGuest);
    }
}