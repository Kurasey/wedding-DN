package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import io.github.kurasey.wedding_invitation.service.GuestService;
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

    public GuestSelfManagementController(FamilyService familyService, GuestService guestService) {
        this.familyService = familyService;
        this.guestService = guestService;
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
            guestService.addNewGuestToFamily(family.getId(), newGuest);
            redirectAttributes.addFlashAttribute("successMessage", "Гость '" + newGuest.getName() + "' успешно добавлен.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/" + personalLink + "/edit";
    }

    @PostMapping("/guests/{guestId}")
    public String updateGuest(@PathVariable String personalLink, @PathVariable Long guestId, @ModelAttribute Guest guestDetails, RedirectAttributes redirectAttributes) {
        System.err.println(guestDetails.isWillAttend());
        guestService.updateGuest(guestId, guestDetails);
        redirectAttributes.addFlashAttribute("successMessage", "Данные гостя '" + guestDetails.getName() + "' успешно обновлены.");
        return "redirect:/" + personalLink + "/edit";
    }

    @PostMapping("/guests/{guestId}/delete")
    public String deleteGuest(@PathVariable String personalLink, @PathVariable Long guestId, RedirectAttributes redirectAttributes) {
        System.err.println(guestId);
        guestService.deleteById(guestId);
        System.err.println(guestId);
        redirectAttributes.addFlashAttribute("successMessage", "Гость успешно удален.");
        return "redirect:/" + personalLink + "/edit";
    }
}