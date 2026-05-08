package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ActionChariteService actionChariteService;
    private final DonService donService;
    private final ParticipationService participationService;
    private final OrganisationService organisationService;

    public HomeController(ActionChariteService actionChariteService,
                         DonService donService,
                         ParticipationService participationService,
                         OrganisationService organisationService) {
        this.actionChariteService = actionChariteService;
        this.donService = donService;
        this.participationService = participationService;
        this.organisationService = organisationService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_ADMIN"::equals);
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }
        }

        // Only public-facing data: published/active actions, approved orgs, confirmed donations
        List<ActionCharite> publicActions = actionChariteService.findAll().stream()
                .filter(a -> a.getStatus() == StatutAction.PUBLIEE || a.getStatus() == StatutAction.EN_COURS)
                .toList();

        List<ActionCharite> recentActions = publicActions.stream()
                .filter(a -> a.getDateCreation() != null)
                .sorted((a1, a2) -> a2.getDateCreation().compareTo(a1.getDateCreation()))
                .limit(6)
                .toList();

        long confirmedDons = donService.findAll().stream()
                .filter(d -> d.getStatus() == StatutDon.CONFIRME)
                .count();

        long approvedOrgs = organisationService.findAll().stream()
                .filter(o -> o.getStatus() == StatusOrganisation.APPROVED)
                .count();

        long totalParticipants = participationService.findAll().size();

        model.addAttribute("totalActions", publicActions.size());
        model.addAttribute("totalDons", confirmedDons);
        model.addAttribute("totalParticipants", totalParticipants);
        model.addAttribute("totalOrganisations", approvedOrgs);
        model.addAttribute("recentActions", recentActions);

        return "index";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        return "redirect:/";
    }
}
