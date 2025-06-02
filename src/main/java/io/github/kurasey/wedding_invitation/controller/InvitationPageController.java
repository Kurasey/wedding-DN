package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/{personalLink}")
//@SessionAttributes("family")
public class InvitationPageController {

    private final FamilyService familyService;

    public InvitationPageController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping
    public String getInvitationPage() {
        return "invitationPage";
    }

    @ModelAttribute("family")
    Family setFamily(@PathVariable("familyCode")String personalLink){
        return familyService.getByPersonalLink(personalLink);
    }
}
