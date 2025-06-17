package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.exception.NotFoundGuestException;
import io.github.kurasey.wedding_invitation.model.ActionSource;
import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import io.github.kurasey.wedding_invitation.service.GuestService;
import io.github.kurasey.wedding_invitation.service.TelegramNotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;

@Controller
@RequestMapping("/{personalLink}")
public class GuestSelfManagementController {

    private final FamilyService familyService;
    private final GuestService guestService;
    private final TelegramNotificationService notificationService; // <-- ДОБАВЛЕНО

    public GuestSelfManagementController(FamilyService familyService, GuestService guestService, TelegramNotificationService notificationService) {
        this.familyService = familyService;
        this.guestService = guestService;
        this.notificationService = notificationService; // <-- ДОБАВЛЕНО
    }

    @GetMapping("/edit")
    public String showGuestManagementPage(@PathVariable String personalLink, Model model) {
        Family family = familyService.getByPersonalLink(personalLink);
        model.addAttribute("family", family);
        model.addAttribute("allBeverages", EnumSet.allOf(Beverage.class));
        boolean canAddGuest = family.getGuests().size() < family.getMaxAvailableGuestCount();
        model.addAttribute("canAddGuest", canAddGuest);
        return "guests-self-edit";
    }

    @PostMapping("/guests/add")
    public String addGuest(@PathVariable String personalLink, @ModelAttribute Guest newGuest, RedirectAttributes redirectAttributes) {
        Family family = familyService.getByPersonalLink(personalLink);
        try {
            Guest addedGuest = guestService.addNewGuestToFamily(family.getId(), newGuest);

            // --- Уведомление о добавлении ---
            notificationService.sendGuestAddedNotification(family, addedGuest, ActionSource.GUEST);

            redirectAttributes.addFlashAttribute("successMessage", "Гость '" + newGuest.getName() + "' успешно добавлен.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/" + personalLink + "/edit";
    }

    @PostMapping("/guests/{guestId}")
    public String updateGuest(@PathVariable String personalLink, @PathVariable Long guestId, @ModelAttribute Guest guestDetails, RedirectAttributes redirectAttributes) {
        Family family = familyService.getByPersonalLink(personalLink);

        // Получаем старый статус
        Guest oldGuest = guestService.findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден"));
        boolean oldStatus = oldGuest.isWillAttend();

        Guest updatedGuest = guestService.updateGuest(guestId, guestDetails);

        // --- Уведомление об изменении статуса ---
        if (oldStatus != updatedGuest.isWillAttend()) {
            notificationService.sendGuestStatusChangedNotification(family, updatedGuest, ActionSource.GUEST);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Данные гостя '" + updatedGuest.getName() + "' успешно обновлены.");
        return "redirect:/" + personalLink + "/edit";
    }

    @PostMapping("/guests/{guestId}/delete")
    public String deleteGuest(@PathVariable String personalLink, @PathVariable Long guestId, RedirectAttributes redirectAttributes) {
        Family family = familyService.getByPersonalLink(personalLink);

        Guest guestToRemove = guestService.findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден"));
        String guestName = guestToRemove.getName();

        guestService.deleteById(guestId);

        // --- Уведомление об удалении ---
        notificationService.sendGuestRemovedNotification(family, guestName, ActionSource.GUEST);

        redirectAttributes.addFlashAttribute("successMessage", "Гость успешно удален.");
        return "redirect:/" + personalLink + "/edit";
    }
}