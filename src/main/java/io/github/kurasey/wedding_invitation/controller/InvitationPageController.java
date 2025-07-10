package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.config.InvitationParametersHolder;
import io.github.kurasey.wedding_invitation.exception.NotFoundFamily;
import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Family;
import io.github.kurasey.wedding_invitation.service.FamilyService;
import io.github.kurasey.wedding_invitation.service.TimelineItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/{personalLink:^(?!.*\\.(?:css|js|ico|png|jpg|gif|svg|txt|ics)$).*$}")
public class InvitationPageController {

    private final FamilyService familyService;
    private final InvitationParametersHolder parametersHolder;
    private final TimelineItemService timelineItemService;

    public InvitationPageController(FamilyService familyService, InvitationParametersHolder parametersHolder, TimelineItemService timelineItemService) {
        this.familyService = familyService;
        this.parametersHolder = parametersHolder;
        this.timelineItemService = timelineItemService;
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
        model.addAttribute("timelineItems", timelineItemService.getAllTimelineItems());
        return "invitationPage";
    }

    @GetMapping("/event.ics")
    public ResponseEntity<String> downloadIcsFile() {
        ZonedDateTime eventStart = parametersHolder.getEventDateTime();
        ZonedDateTime eventEnd = eventStart.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        String eventSummary = "Свадьба " + parametersHolder.getGroomName() + " и " + parametersHolder.getBrideName();
        String eventLocation = parametersHolder.getVenueName() + ", " + parametersHolder.getVenueAddress();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

        String icsContent = String.join("\r\n",
                "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "PRODID:-//dya-wedding.duckdns.org",
                "BEGIN:VEVENT",
                "UID:" + UUID.randomUUID(),
                "DTSTAMP:" + formatter.format(ZonedDateTime.now()),
                "DTSTART:" + formatter.format(eventStart),
                "DTEND:" + formatter.format(eventEnd),
                "SUMMARY:" + eventSummary,
                "LOCATION:" + eventLocation,
                "DESCRIPTION:" + "Не забудьте подтвердить свое присутствие!",
                "END:VEVENT",
                "END:VCALENDAR"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=utf-8");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"wedding_event.ics\"");
        return new ResponseEntity<>(icsContent, headers, HttpStatus.OK);

    }
}
