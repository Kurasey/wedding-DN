package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.config.InvitationParametersHolder;
import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (!family.isActive()){
            throw new NotFoundFamily("Family with link " + personalLink + " is not active.");
        }
        model.addAttribute("family", family);
        model.addAttribute("invitation", parametersHolder);
        boolean hasResponded = !family.getGuests().isEmpty();
        model.addAttribute("hasResponded", hasResponded);
        model.addAttribute("allBeverages",
                Arrays.stream(Beverage.values())
                        .map(b -> Map.of("id", b.name(), "displayName", b.getName()))
                        .collect(Collectors.toList()));
        return "invitationPage";
    }
}
