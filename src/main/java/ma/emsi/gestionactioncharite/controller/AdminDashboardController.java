package ma.emsi.gestionactioncharite.controller;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.service.AdminDashboardService;
import ma.emsi.gestionactioncharite.service.OrganisationService;
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
@PreAuthorize("hasAnyRole('ADMIN', 'ORG_ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final OrganisationService organisationService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboardData(principal.getUsername()));
        
        boolean isSuperAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isSuperAdmin) {
            model.addAttribute("pendingOrgs", organisationService.findByStatus(StatusOrganisation.PENDING));
        }
        
        model.addAttribute("isSuperAdmin", isSuperAdmin);
        return "admin/dashboard";
    }
}
