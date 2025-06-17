package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.controller.dto.GuestDto;
import io.github.kurasey.wedding_invitation.controller.dto.RsvpRequestDto;
import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.repository.FamilyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    private final FamilyRepository familyRepository;
    private final TelegramNotificationService notificationService;

    public RsvpService(FamilyRepository familyRepository, TelegramNotificationService notificationService) {
        this.familyRepository = familyRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void processRsvp(String personalLink, RsvpRequestDto rsvpRequest) {
        Family family = familyRepository.findByPersonalLink(personalLink)
                .orElseThrow(() -> new NotFoundFamily("Семья с ссылкой " + personalLink + " не найдена."));

        if (!family.getGuests().isEmpty()) {
            throw new IllegalStateException("Попытка перезаписать существующих гостей через основной RSVP. Используйте страницу редактирования.");
        }

        family.setPhone(rsvpRequest.getContactPhone());

        List<Guest> newGuests = new ArrayList<>();

        for (GuestDto guestDto : rsvpRequest.getGuests()) {
            Set<Beverage> beverages = guestDto.getDrinks().stream()
                    .map(Beverage::fromDisplayName)
                    .collect(Collectors.toSet());

            Guest guest = new Guest(family, guestDto.getName(), beverages);
            guest.setWillAttend(true);
            family.getGuests().add(guest);
            newGuests.add(guest);
        }

        familyRepository.save(family);

        notificationService.sendRsvpNotification(family, newGuests);
    }
}