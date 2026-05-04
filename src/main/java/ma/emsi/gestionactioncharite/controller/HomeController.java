package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.Participation;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String home(Model model) {
        // Get statistics
        List<ActionCharite> allActions = actionChariteService.findAll();
        List<Don> allDons = donService.findAll();
        List<Participation> allParticipations = participationService.findAll();
        List<Organisation> allOrganisations = organisationService.findAll();

        // Get recent actions (last 6)
        List<ActionCharite> recentActions = allActions.stream()
                .sorted((a1, a2) -> a2.getDateCreation().compareTo(a1.getDateCreation()))
                .limit(6)
                .toList();

        // Add model attributes
        model.addAttribute("totalActions", allActions.size());
        model.addAttribute("totalDons", allDons.size());
        model.addAttribute("totalParticipants", allParticipations.size());
        model.addAttribute("totalOrganisations", allOrganisations.size());
        model.addAttribute("recentActions", recentActions);

        return "index";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        return "redirect:/";
    }
}
