package ma.emsi.gestionactioncharite.controller;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboardData(principal.getUsername()));
        return "admin/dashboard";
    }
}
