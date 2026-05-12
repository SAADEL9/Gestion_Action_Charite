package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.dto.UserRequestDTO;
import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public String me(@AuthenticationPrincipal UserDetails principal) {
        var user = userService.findEntityByEmail(principal.getUsername());
        return "redirect:/users/" + user.getId();
    }

    // ─── SUPER_ADMIN: list all users ───────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    // ─── View profile (own or super_admin) ─────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String findById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user/detail";
    }

    @PostMapping("/me/request-organisation")
    @PreAuthorize("isAuthenticated()")
    public String requestOrganisation(@AuthenticationPrincipal UserDetails principal) {
        var user = userService.findEntityByEmail(principal.getUsername());
        userService.requestOrganisationAccess(user.getId());
        return "redirect:/users/" + user.getId() + "?requested=true";
    }

    @PostMapping("/{id}/approve-organisation")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveOrganisationRequest(@PathVariable Long id) {
        userService.approveOrganisationAccess(id);
        return "redirect:/users";
    }

    // ─── Show edit form ─────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user/form";
    }

    // ─── Submit edit form ───────────────────────────────────────────
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String update(@PathVariable Long id, @ModelAttribute UserRequestDTO dto) {
        userService.update(id, dto);
        return "redirect:/users/" + id;
    }

    // ─── SUPER_ADMIN: deactivate user ──────────────────────────────
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return "redirect:/users";
    }

    // ─── SUPER_ADMIN: activate user ────────────────────────────────
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public String activate(@PathVariable Long id) {
        userService.activate(id);
        return "redirect:/users";
    }

    // ─── SUPER_ADMIN: change role ───────────────────────────────────
    @PostMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeRole(@PathVariable Long id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return "redirect:/users";
    }
}
