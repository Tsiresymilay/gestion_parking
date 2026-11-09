package com.example.parking.controller.web;

import com.example.parking.dto.request.UserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.Role;
import com.example.parking.entity.UserType;
import com.example.parking.exception.BusinessException;
import com.example.parking.service.UserService;
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
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("allRoles")
    public Role[] allRoles() {
        return Role.values();
    }

    @ModelAttribute("allUserTypes")
    public UserType[] allUserTypes() {
        return UserType.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Role role,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<UserResponse> users = userService.search(keyword, role,
                PageRequest.of(page, 10, Sort.by("username")));
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleFilter", role);
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") UserRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "users/form";
        }
        try {
            userService.create(request);
            redirectAttributes.addFlashAttribute("success", "Utilisateur cree avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("editId", id);
        return "users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") UserRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editId", id);
            return "users/form";
        }
        try {
            userService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Utilisateur modifie avec succes");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprime");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }
}
