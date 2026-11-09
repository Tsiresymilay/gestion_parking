package com.example.parking.controller.web;

import com.example.parking.dto.request.EntryRequest;
import com.example.parking.dto.request.ExitRequest;
import com.example.parking.dto.response.SessionResponse;
import com.example.parking.entity.SessionStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/operations")
public class OperationWebController {

    private final ParkingService parkingService;

    public OperationWebController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<SessionResponse> sessions = parkingService.search(null, null,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "entryTime")));
        model.addAttribute("sessions", sessions);
        model.addAttribute("activeSessions", parkingService.findActive());
        if (!model.containsAttribute("entryRequest")) {
            model.addAttribute("entryRequest", new EntryRequest());
        }
        if (!model.containsAttribute("exitRequest")) {
            model.addAttribute("exitRequest", new ExitRequest());
        }
        return "operations/index";
    }

    @PostMapping("/entries")
    public String enter(@Valid @ModelAttribute("entryRequest") EntryRequest request,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Immatriculation obligatoire");
            return "redirect:/operations";
        }
        try {
            SessionResponse session = parkingService.enter(request);
            redirectAttributes.addFlashAttribute("success",
                    "Entree enregistree - place " + session.getSpaceNumber());
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/operations";
    }

    @PostMapping("/exits")
    public String exit(@Valid @ModelAttribute("exitRequest") ExitRequest request,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Immatriculation obligatoire");
            return "redirect:/operations";
        }
        try {
            SessionResponse session = parkingService.exit(request);
            redirectAttributes.addFlashAttribute("success",
                    "Sortie enregistree - montant : " + session.getAmount());
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/operations";
    }
}
