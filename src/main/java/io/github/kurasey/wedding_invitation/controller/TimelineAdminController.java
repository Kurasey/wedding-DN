package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.model.TimelineItem;
import io.github.kurasey.wedding_invitation.service.IconService;
import io.github.kurasey.wedding_invitation.service.TimelineItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/timeline")
public class TimelineAdminController {

    private final TimelineItemService timelineService;
    private final IconService iconService;

    public TimelineAdminController(TimelineItemService timelineService, IconService iconService) {
        this.timelineService = timelineService;
        this.iconService = iconService;
    }

    @ModelAttribute("activePage")
    public String activePage() {
        return "timeline";
    }

    @GetMapping
    public String showTimelineManagement(Model model) {
        model.addAttribute("timelineItems", timelineService.getAllTimelineItems());
        model.addAttribute("formItem", new TimelineItem());
        model.addAttribute("iconPacks", iconService.getAvailablePacks());
        return "admin/timeline";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<TimelineItem> getItemData(@PathVariable Long id) {
        return timelineService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/icons/{type}/{packName}")
    @ResponseBody
    public List<String> getIcons(@PathVariable String type, @PathVariable String packName) {
        return iconService.getIconUrls(type, packName);
    }

    @PostMapping("/save")
    public String saveOrUpdateItem(@ModelAttribute("formItem") TimelineItem item, RedirectAttributes redirectAttributes) {
        boolean isNew = item.getId() == null;
        timelineService.save(item);
        String message = "Пункт '" + item.getTitle() + "' успешно " + (isNew ? "добавлен." : "обновлен.");
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/timeline";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        timelineService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пункт успешно удален.");
        return "redirect:/admin/timeline";
    }

    @PostMapping("/moveup/{id}")
    public String moveItemUp(@PathVariable Long id) {
        timelineService.moveUp(id);
        return "redirect:/admin/timeline";
    }

    @PostMapping("/movedown/{id}")
    public String moveItemDown(@PathVariable Long id) {
        timelineService.moveDown(id);
        return "redirect:/admin/timeline";
    }
}