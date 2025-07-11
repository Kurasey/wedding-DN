package io.github.kaurami.wems.service;

import io.github.kaurami.wems.controller.dto.GuestDto;
import io.github.kaurami.wems.controller.dto.RsvpRequestDto;
import io.github.kaurami.wems.model.ActionSource;
import io.github.kaurami.wems.model.Beverage;
import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.model.Guest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    private final FamilyService familyService;
    private final TelegramNotificationService notificationService;

    public RsvpService(FamilyService familyService, TelegramNotificationService notificationService) {
        this.familyService = familyService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void processRsvp(String personalLink, RsvpRequestDto rsvpRequest) {
        Family family = familyService.getByPersonalLink(personalLink);

        if (rsvpRequest.getGuests().size() > family.getMaxAvailableGuestCount()) {
            throw new IllegalStateException("Превышен лимит гостей для этой семьи.");
        }

        boolean isFirstRsvp = family.getGuests().isEmpty();

        if (rsvpRequest.getContactPhone() != null && !rsvpRequest.getContactPhone().isBlank()) {
            family.setPhone(rsvpRequest.getContactPhone());
        }

        // --- ЛОГИКА СРАВНЕНИЯ И УВЕДОМЛЕНИЙ ---

        // 1. Создаем карту существующих гостей для быстрого доступа и отслеживания удаленных
        Map<Long, Guest> oldGuestsMap = family.getGuests().stream()
                .collect(Collectors.toMap(Guest::getId, Function.identity()));

        // 2. Списки для отслеживания изменений
        List<Guest> finalGuestList = new ArrayList<>();
        List<Guest> addedGuests = new ArrayList<>();
        List<Guest> statusChangedGuests = new ArrayList<>();

        // 3. Обрабатываем входящих гостей
        for (GuestDto guestDto : rsvpRequest.getGuests()) {
            Set<Beverage> beverages = guestDto.getDrinks().stream()
                    .map(Beverage::fromDisplayName)
                    .collect(Collectors.toSet());

            Guest guest;
            if (guestDto.getId() != null) {
                // Это существующий гость. Находим его и удаляем из карты "старых".
                guest = oldGuestsMap.remove(guestDto.getId());
                if (guest == null) {
                    throw new IllegalStateException("Попытка обновить несуществующего гостя с ID: " + guestDto.getId());
                }

                // Проверяем, изменился ли статус присутствия
                if (guest.isWillAttend() != guestDto.isWillAttend()) {
                    statusChangedGuests.add(guest); // Добавляем в список для уведомления
                }

            } else {
                // новый гость
                guest = new Guest();
                guest.setFamily(family);
                addedGuests.add(guest);
            }

            guest.setName(guestDto.getName());
            guest.setBeverages(beverages);
            guest.setWillAttend(guestDto.isWillAttend());

            finalGuestList.add(guest);
        }

        // 4. Все, кто остался в карте oldGuestsMap - были удалены
        List<Guest> removedGuests = new ArrayList<>(oldGuestsMap.values());

        // 5. Обновляем список гостей в семье
        family.getGuests().clear();
        family.getGuests().addAll(finalGuestList);
        familyService.persist(family);

        // 6. Отправляем уведомления
        if (isFirstRsvp) {
            notificationService.sendRsvpNotification(family, finalGuestList);
        } else {
            removedGuests.forEach(guest -> notificationService.sendGuestRemovedNotification(family, guest.getName(), ActionSource.GUEST));
            addedGuests.forEach(guest -> notificationService.sendGuestAddedNotification(family, guest, ActionSource.GUEST));
            statusChangedGuests.forEach(guest -> notificationService.sendGuestStatusChangedNotification(family, guest, ActionSource.GUEST));
        }
    }

    public List<GuestDto> getGuestDetailsForFamily(String personalLink) {
        Family family = familyService.getByPersonalLink(personalLink);

        return family.getGuests().stream()
                .map(guest -> {
                    GuestDto dto = new GuestDto();
                    dto.setId(guest.getId());
                    dto.setName(guest.getName());
                    dto.setDrinks(guest.getBeverages().stream().map(Beverage::getName).collect(Collectors.toList()));
                    dto.setWillAttend(guest.isWillAttend());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}