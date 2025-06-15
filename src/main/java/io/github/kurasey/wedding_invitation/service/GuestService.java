package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.repository.GuestRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
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
        List<Guest> guests = findAllOrderByFamily();

        if (attending != null) {
            guests = guests.stream().filter(g -> g.isWillAttend() == attending).collect(Collectors.toList());
        }
        if (transfer != null) {
            guests = guests.stream().filter(g -> g.getFamily().isTransferRequired() == transfer).collect(Collectors.toList());
        }
        if (placement != null) {
            guests = guests.stream().filter(g -> g.getFamily().isPlacementRequired() == placement).collect(Collectors.toList());
        }

        return guests;
    }

    public Map<String, Object> getDashboardStats() {
        List<Guest> allGuests = findAll();
        long confirmedGuestsCount = allGuests.stream().filter(Guest::isWillAttend).count();

        // Получаем уникальные семьи, у которых есть хотя бы один подтвержденный гость
        Set<Family> familiesWithConfirmedGuests = allGuests.stream()
                .filter(Guest::isWillAttend)
                .map(Guest::getFamily)
                .collect(Collectors.toSet());

        long transferRequiredCount = familiesWithConfirmedGuests.stream().filter(Family::isTransferRequired).count();
        long placementRequiredCount = familiesWithConfirmedGuests.stream().filter(Family::isPlacementRequired).count();

        Map<Beverage, Long> beverageStats = allGuests.stream()
                .filter(Guest::isWillAttend)
                .flatMap(guest -> guest.getBeverages().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long totalGuests = allGuests.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGuests", totalGuests);
        stats.put("confirmedGuests", confirmedGuestsCount);
        stats.put("beverageStats", beverageStats);
        stats.put("allBeverages", EnumSet.allOf(Beverage.class));
        stats.put("transferRequiredCount", transferRequiredCount);
        stats.put("placementRequiredCount", placementRequiredCount);

        return stats;
    }
}