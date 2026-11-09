package com.example.parking.controller.web;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.ReservationStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.ReservationService;
import com.example.parking.service.SpaceService;
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
@RequestMapping("/reservations")
public class ReservationWebController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final SpaceService spaceService;

    public ReservationWebController(ReservationService reservationService, UserService userService,
                                    VehicleService vehicleService, SpaceService spaceService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.spaceService = spaceService;
    }

    @ModelAttribute("allStatus")
    public ReservationStatus[] allStatus() {
        return ReservationStatus.values();
    }

    private void formData(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("availableSpaces", spaceService.findAvailable());
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) ReservationStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<ReservationResponse> reservations = reservationService.search(keyword, status,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "expectedArrival")));
        model.addAttribute("reservations", reservations);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        return "reservations/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("reservation", new ReservationRequest());
        formData(model);
        return "reservations/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("reservation") ReservationRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            formData(model);
            return "reservations/form";
        }
        try {
            reservationService.create(request);
            redirectAttributes.addFlashAttribute("success", "Reservation creee avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirm(id);
            redirectAttributes.addFlashAttribute("success", "Reservation confirmee");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancel(id);
            redirectAttributes.addFlashAttribute("success", "Reservation annulee");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Reservation supprimee");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservations";
    }
}
