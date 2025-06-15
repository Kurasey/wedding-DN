package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.config.InvitationParametersHolder;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/{personalLink}")
public class InvitationPageController {

    private final FamilyService familyService;
    private final InvitationParametersHolder parametersHolder;

    public InvitationPageController(FamilyService familyService, InvitationParametersHolder parametersHolder) {
        this.familyService = familyService;
        this.parametersHolder = parametersHolder;
    }

    @GetMapping
    public String getInvitationPage(@PathVariable String personalLink, Model model) {
        Family family = familyService.getByPersonalLink(personalLink);
        model.addAttribute("family", family);
        model.addAttribute("invitation", parametersHolder);
        boolean hasResponded = !family.getGuests().isEmpty();
        model.addAttribute("hasResponded", hasResponded);
        return "invitationPage";
    }
}
