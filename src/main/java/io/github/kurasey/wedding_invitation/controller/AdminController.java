package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.model.VisitHistoryRecord;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import io.github.kurasey.wedding_invitation.service.GuestService;
import io.github.kurasey.wedding_invitation.service.VisitHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GuestService guestService;
    private final FamilyService familyService;
    private final VisitHistoryService historyService;

    public AdminController(GuestService guestService, FamilyService familyService, VisitHistoryService historyService) {
        this.guestService = guestService;
        this.familyService = familyService;
        this.historyService = historyService;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/families")
    public String listFamilies(Model model) {
        model.addAttribute(familyService.getAllFamilies());
        return "/admin/families/list";
    }

///*    @GetMapping("/families/new")
//    public String showNewFamilyForm(Model model) {
//        model.addAttribute("family", new Family());
//        return "admin/families/form";
//    }*/

/*    @GetMapping("/families/edit/{id}")
    public String showEditFamilyForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute(familyService.getFamilyById(id));
        } catch (NotFoundFamily notFoundFamily) {

        }
    }*/


   /* @ModelAttribute("families")
    List<Family> families() {
        return familyService.getAllFamilies();
    }

    @ModelAttribute("guests")
    List<Guest> guests() {
        return guestService.findAll();
    }

    @ModelAttribute("history")
    List<VisitHistoryRecord> visitHistoryRecords() {
        return historyService.findAll();
    }*/

}
