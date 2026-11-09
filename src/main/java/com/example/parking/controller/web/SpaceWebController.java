package com.example.parking.controller.web;

import com.example.parking.dto.request.SpaceRequest;
import com.example.parking.dto.response.SpaceResponse;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.SpaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/spaces")
public class SpaceWebController {

    private final SpaceService spaceService;

    public SpaceWebController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @ModelAttribute("allTypes")
    public SpaceType[] allTypes() {
        return SpaceType.values();
    }

    @ModelAttribute("allStatus")
    public SpaceStatus[] allStatus() {
        return SpaceStatus.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) SpaceStatus status,
                       @RequestParam(required = false) SpaceType type,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<SpaceResponse> spaces = spaceService.search(keyword, status, type, null,
                PageRequest.of(page, 10, Sort.by("number")));
        model.addAttribute("spaces", spaces);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("typeFilter", type);
        return "spaces/list";
    }

    @GetMapping("/map")
    public String map(Model model) {
        model.addAttribute("spaces", spaceService.findAll());
        return "spaces/map";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("space", new SpaceRequest());
        return "spaces/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("space") SpaceRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "spaces/form";
        }
        try {
            spaceService.create(request);
            redirectAttributes.addFlashAttribute("success", "Place creee avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/spaces";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("space", spaceService.findById(id));
        model.addAttribute("editId", id);
        return "spaces/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("space") SpaceRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editId", id);
            return "spaces/form";
        }
        try {
            spaceService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Place modifiee avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/spaces";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            spaceService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Place supprimee");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/spaces";
    }
}
