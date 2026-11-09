package com.example.parking.controller.web;

import com.example.parking.dto.request.VehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.VehicleType;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.UserService;
import com.example.parking.service.VehicleService;
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

@Controller
@RequestMapping("/vehicles")
public class VehicleWebController {

    private final VehicleService vehicleService;
    private final UserService userService;

    public VehicleWebController(VehicleService vehicleService, UserService userService) {
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @ModelAttribute("allTypes")
    public VehicleType[] allTypes() {
        return VehicleType.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) VehicleType type,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<VehicleResponse> vehicles = vehicleService.search(keyword, type,
                PageRequest.of(page, 10, Sort.by("plateNumber")));
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("keyword", keyword);
        model.addAttribute("typeFilter", type);
        return "vehicles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vehicle", new VehicleRequest());
        model.addAttribute("users", userService.findAll());
        return "vehicles/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vehicle") VehicleRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "vehicles/form";
        }
        try {
            vehicleService.create(request);
            redirectAttributes.addFlashAttribute("success", "Vehicule cree avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehicles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.findById(id));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("editId", id);
        return "vehicles/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("vehicle") VehicleRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("editId", id);
            return "vehicles/form";
        }
        try {
            vehicleService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Vehicule modifie avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Vehicule supprime");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehicles";
    }
}
