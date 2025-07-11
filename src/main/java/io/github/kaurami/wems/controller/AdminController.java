package io.github.kaurami.wems.controller;

import io.github.kaurami.wems.config.InvitationParametersHolder;
import io.github.kaurami.wems.model.Family;
import io.github.kaurami.wems.service.FamilyService;
import io.github.kaurami.wems.service.GuestService;
import io.github.kaurami.wems.service.VisitHistoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GuestService guestService;
    private final FamilyService familyService;
    private final VisitHistoryService historyService;
    private final InvitationParametersHolder parametersHolder;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(GuestService guestService, FamilyService familyService,
                           VisitHistoryService historyService, InvitationParametersHolder parametersHolder) {
        this.guestService = guestService;
        this.familyService = familyService;
        this.historyService = historyService;
        this.parametersHolder = parametersHolder;
    }

    private String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    @GetMapping("/families")
    public String listFamilies(Model model, @RequestParam(defaultValue = "false", name = "showUnconfirmedOnly") boolean showUnconfirmedOnly) {
        List<Family> families;
        if (showUnconfirmedOnly) {
            families = familyService.findFamiliesWithNoConfirmedGuests();
        } else {
            families = familyService.getAllFamilies();
        }
        model.addAttribute("families", families);
        model.addAttribute("showUnconfirmedOnly", showUnconfirmedOnly);
        model.addAttribute("activePage", "families");
        return "admin/families";
    }

    @GetMapping("/families/new")
    public String showCreateFamilyForm(Model model) {
        Family family = new Family();
        family.setConfirmationDeadline(parametersHolder.getConfirmationDeadline());
        family.setActive(true);
        family.setMaxAvailableGuestCount(2);
        model.addAttribute("family", family);
        model.addAttribute("pageTitle", "Добавить новую семью");
        model.addAttribute("activePage", "families");
        return "admin/family-form";
    }

    @PostMapping("/families")
    public String createFamily(@Valid @ModelAttribute("family") Family family,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Добавить новую семью");
            model.addAttribute("activePage", "families");
            return "admin/family-form";
        }
        Family savedFamily = familyService.createFamily(family);
        redirectAttributes.addFlashAttribute("successMessage", "Семья '" + family.getName() + "' успешно создана! Код: " + savedFamily.getPersonalLink());
        return "redirect:/admin/families";
    }

    @GetMapping("/families/{id}/edit")
    public String showEditFamilyForm(@PathVariable("id") Long id, Model model) {
        Family family = familyService.getFamilyById(id);
        model.addAttribute("family", family);
        model.addAttribute("pageTitle", "Редактировать семью: " + family.getName());
        model.addAttribute("activePage", "families");
        model.addAttribute("baseUrl", getBaseUrl());
        return "admin/family-form";
    }

    @PostMapping("/families/{id}/edit")
    public String updateFamily(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("family") Family familyDetails,
                               BindingResult result,
                               RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            familyDetails.setId(id);
            model.addAttribute("pageTitle", "Редактировать семью: " + (familyDetails.getName() != null ? familyDetails.getName() : ""));
            model.addAttribute("activePage", "families");
            model.addAttribute("baseUrl", getBaseUrl());
            return "admin/family-form";
        }
        familyService.updateFamily(id, familyDetails);
        redirectAttributes.addFlashAttribute("successMessage", "Семья '" + familyDetails.getName() + "' успешно обновлена!");
        return "redirect:/admin/families";
    }

    @PostMapping("/families/{id}/delete")
    public String deleteFamily(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Family family = familyService.getFamilyById(id);
            familyService.deleteFamily(id);
            redirectAttributes.addFlashAttribute("successMessage", "Семья '" + family.getName() + "' и вся связанная информация успешно удалены!");
        } catch (DataIntegrityViolationException e) {
            logger.error("Could not delete family with ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Невозможно удалить семью. Возможно, на нее еще есть ссылки в других частях системы.");
        } catch (Exception e) {
            logger.error("An unexpected error occurred while deleting family with ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла непредвиденная ошибка при удалении семьи.");
        }
        return "redirect:/admin/families";
    }

    @GetMapping("/families/{id}")
    public String viewFamilyDetails(@PathVariable("id") Long id, Model model) {
        Family family = familyService.getFamilyById(id);
        model.addAttribute("family", family);
        model.addAttribute("activePage", "families");
        return "admin/family-details";
    }

    @GetMapping("/guests")
    public String listAllGuests(Model model,
                                @RequestParam(name = "attending", required = false) Boolean attending,
                                @RequestParam(name = "transfer", required = false) Boolean transfer,
                                @RequestParam(name = "placement", required = false) Boolean placement) {

        model.addAttribute("guests", guestService.findWithFilters(attending, transfer, placement));
        model.addAttribute("filterAttending", attending);
        model.addAttribute("filterTransfer", transfer);
        model.addAttribute("filterPlacement", placement);
        model.addAttribute("activePage", "guests");
        return "admin/guests";
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("stats", guestService.getDashboardStats());
        model.addAttribute("totalFamilies", familyService.getAllFamilies().size());
        model.addAttribute("visitCount", historyService.countAll());
        return "admin/dashboard";
    }


    @GetMapping("/history")
    public String viewVisitHistory(Model model, @RequestParam(required = false) Long familyId) {
        if (familyId != null) {
            Family family = familyService.getFamilyById(familyId);
            model.addAttribute("historyRecords", historyService.historyByPersonalLink(family.getPersonalLink()));
        } else {
            model.addAttribute("historyRecords", historyService.findAll());
        }

        model.addAttribute("allFamilies", familyService.getAllFamilies());
        model.addAttribute("selectedFamilyId", familyId);
        model.addAttribute("activePage", "history");
        return "admin/history";
    }
}