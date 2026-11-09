package com.example.parking.controller.web;

import com.example.parking.dto.request.SubscriptionRequest;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.SubscriptionStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.SubscriptionService;
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
@RequestMapping("/subscriptions")
public class SubscriptionWebController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public SubscriptionWebController(SubscriptionService subscriptionService, UserService userService,
                                     VehicleService vehicleService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @ModelAttribute("allStatus")
    public SubscriptionStatus[] allStatus() {
        return SubscriptionStatus.values();
    }

    private void formData(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("vehicles", vehicleService.findAll());
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) SubscriptionStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.search(keyword, status,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "startDate")));
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        return "subscriptions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("subscription", new SubscriptionRequest());
        formData(model);
        return "subscriptions/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("subscription") SubscriptionRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            formData(model);
            return "subscriptions/form";
        }
        try {
            subscriptionService.create(request);
            redirectAttributes.addFlashAttribute("success", "Abonnement cree avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subscriptionService.cancel(id);
            redirectAttributes.addFlashAttribute("success", "Abonnement annule");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/subscriptions";
    }
}
