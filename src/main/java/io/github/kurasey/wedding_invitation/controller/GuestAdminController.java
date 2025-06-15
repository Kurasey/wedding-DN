package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.exception.NotFoundGuestException;
import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import io.github.kurasey.wedding_invitation.service.GuestService;
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

    public GuestAdminController(GuestService guestService, FamilyService familyService) {
        this.guestService = guestService;
        this.familyService = familyService;
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
            addCommonAttributes(model); // <-- ДОБАВЛЕНО
            return "admin/guest-form";
        }
        familyService.addGuestToFamily(familyId, guest);
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
        guestDetails.setId(guestId);
        guestDetails.setFamily(findFamily(familyId));
        guestService.save(guestDetails);
        redirectAttributes.addFlashAttribute("successMessage", "Данные гостя '" + guestDetails.getName() + "' обновлены.");
        return "redirect:/admin/families/" + familyId;
    }

    @PostMapping("/{guestId}/delete")
    public String deleteGuest(@PathVariable("familyId") Long familyId,
                              @PathVariable("guestId") Long guestId,
                              RedirectAttributes redirectAttributes) {
        familyService.removeGuestFromFamily(familyId, guestId);
        redirectAttributes.addFlashAttribute("successMessage", "Гость удален.");
        return "redirect:/admin/families/" + familyId;
    }
}