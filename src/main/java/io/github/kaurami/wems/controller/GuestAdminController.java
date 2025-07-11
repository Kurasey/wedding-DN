package io.github.kaurami.wems.controller;

import io.github.kaurami.wems.exception.NotFoundGuestException;
import io.github.kaurami.wems.model.ActionSource;
import io.github.kaurami.wems.model.Beverage;
import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.model.Guest;
import io.github.kaurami.wems.service.FamilyService;
import io.github.kaurami.wems.service.GuestService;
import io.github.kaurami.wems.service.TelegramNotificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;

@Controller
@RequestMapping("/admin/families/{familyId}/guests")
public class GuestAdminController {

    private final GuestService guestService;
    private final FamilyService familyService;
    private final TelegramNotificationService notificationService;

    public GuestAdminController(GuestService guestService, FamilyService familyService, TelegramNotificationService notificationService) {
        this.guestService = guestService;
        this.familyService = familyService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("family")
    public Family findFamily(@PathVariable("familyId") Long familyId) {
        return familyService.getFamilyById(familyId);
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("allBeverages", EnumSet.allOf(Beverage.class));
        model.addAttribute("activePage", "families");
    }

    @GetMapping("/new")
    public String showAddGuestForm(@PathVariable("familyId") Long familyId, Model model) {
        Guest guest = new Guest();
        guest.setFamily(findFamily(familyId));
        guest.setWillAttend(true);
        model.addAttribute("guest", guest);
        addCommonAttributes(model);
        return "admin/guest-form";
    }

    @PostMapping
    public String addGuest(@PathVariable("familyId") Long familyId,
                           @Valid @ModelAttribute("guest") Guest guest,
                           BindingResult result,
                           RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            addCommonAttributes(model);
            return "admin/guest-form";
        }
        familyService.addGuestToFamily(familyId, guest);

        // --- Уведомление о добавлении ---
        notificationService.sendGuestAddedNotification(findFamily(familyId), guest, ActionSource.ADMIN);

        redirectAttributes.addFlashAttribute("successMessage", "Гость '" + guest.getName() + "' успешно добавлен.");
        return "redirect:/admin/families/" + familyId;
    }

    @GetMapping("/{guestId}/edit")
    public String showEditGuestForm(@PathVariable("guestId") Long guestId, Model model) {
        Guest guest = guestService.findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден"));
        model.addAttribute("guest", guest);
        addCommonAttributes(model);
        return "admin/guest-form";
    }

    @PostMapping("/{guestId}/edit")
    public String updateGuest(@PathVariable("familyId") Long familyId,
                              @PathVariable("guestId") Long guestId,
                              @Valid @ModelAttribute("guest") Guest guestDetails,
                              BindingResult result,
                              RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            addCommonAttributes(model);
            guestDetails.setId(guestId);
            guestDetails.setFamily(findFamily(familyId));
            return "admin/guest-form";
        }

        // Получаем старое состояние гостя для сравнения
        Guest oldGuest = guestService.findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден"));
        boolean oldStatus = oldGuest.isWillAttend();

        guestDetails.setId(guestId);
        guestDetails.setFamily(findFamily(familyId));
        Guest updatedGuest = guestService.save(guestDetails);

        // --- Уведомление об изменении статуса ---
        if (oldStatus != updatedGuest.isWillAttend()) {
            notificationService.sendGuestStatusChangedNotification(updatedGuest.getFamily(), updatedGuest, ActionSource.ADMIN);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Данные гостя '" + guestDetails.getName() + "' обновлены.");
        return "redirect:/admin/families/" + familyId;
    }

    @PostMapping("/{guestId}/delete")
    public String deleteGuest(@PathVariable("familyId") Long familyId,
                              @PathVariable("guestId") Long guestId,
                              RedirectAttributes redirectAttributes) {
        // Получаем гостя ПЕРЕД удалением, чтобы знать его имя
        Guest guestToRemove = guestService.findById(guestId)
                .orElseThrow(() -> new NotFoundGuestException("Гость с ID " + guestId + " не найден для удаления."));
        String guestName = guestToRemove.getName();
        Family family = guestToRemove.getFamily();

        familyService.removeGuestFromFamily(familyId, guestId);

        // --- Уведомление об удалении ---
        notificationService.sendGuestRemovedNotification(family, guestName, ActionSource.ADMIN);

        redirectAttributes.addFlashAttribute("successMessage", "Гость удален.");
        return "redirect:/admin/families/" + familyId;
    }
}