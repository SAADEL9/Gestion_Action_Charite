package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.Categorie;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import ma.emsi.gestionactioncharite.service.CategorieService;
import ma.emsi.gestionactioncharite.service.DonService;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.ParticipationService;
import ma.emsi.gestionactioncharite.view.HomeActionView;
import ma.emsi.gestionactioncharite.view.HomeCategoryView;
import ma.emsi.gestionactioncharite.view.HomeOrganisationView;
import ma.emsi.gestionactioncharite.view.StepView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class HomeController {

    private final ActionChariteService actionChariteService;
    private final DonService donService;
    private final ParticipationService participationService;
    private final OrganisationService organisationService;
    private final CategorieService categorieService;

    public HomeController(ActionChariteService actionChariteService,
                         DonService donService,
                         ParticipationService participationService,
                         OrganisationService organisationService,
                         CategorieService categorieService) {
        this.actionChariteService = actionChariteService;
        this.donService = donService;
        this.participationService = participationService;
        this.organisationService = organisationService;
        this.categorieService = categorieService;
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
        model.addAttribute("homeCategories", buildCategoryViews());
        model.addAttribute("featuredProjects", recentActions.stream()
                .limit(4)
                .map(this::toHomeActionView)
                .toList());
        model.addAttribute("featuredOrganisation", organisationService.findAll().stream()
                .filter(o -> o.getStatus() == StatusOrganisation.APPROVED)
                .sorted((o1, o2) -> Long.compare(countVisibleActions(o2), countVisibleActions(o1)))
                .findFirst()
                .map(this::toHomeOrganisationView)
                .orElse(null));
        model.addAttribute("howItWorks", List.of(
                new StepView("bi bi-compass", "1. Decouvrez", "Trouvez des actions qui vous inspirent."),
                new StepView("bi bi-hand-index-thumb", "2. Participez", "Inscrivez-vous ou faites un don en ligne."),
                new StepView("bi bi-stars", "3. Faites un impact", "Suivez l'impact de vos contributions.")
        ));

        return "index";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        return "redirect:/";
    }

    private List<HomeCategoryView> buildCategoryViews() {
        return categorieService.findAll().stream()
                .map(category -> new HomeCategoryView(
                        category.getNom(),
                        countPublicActions(category),
                        resolveCategoryIcon(category.getNom()),
                        resolveCategoryColor(category.getNom())
                ))
                .filter(category -> category.count() > 0)
                .sorted((c1, c2) -> Long.compare(c2.count(), c1.count()))
                .limit(5)
                .toList();
    }

    private long countPublicActions(Categorie category) {
        if (category.getActions() == null) {
            return 0;
        }
        return category.getActions().stream()
                .filter(this::isPublicAction)
                .count();
    }

    private boolean isPublicAction(ActionCharite action) {
        return action != null && (action.getStatus() == StatutAction.PUBLIEE || action.getStatus() == StatutAction.EN_COURS);
    }

    private HomeActionView toHomeActionView(ActionCharite action) {
        int progress = computeProgress(action);
        return new HomeActionView(
                action.getId(),
                resolveActionImage(action),
                Optional.ofNullable(action.getCategorie()).map(Categorie::getNom).orElse("Action"),
                action.getTitre(),
                Optional.ofNullable(action.getLieu()).filter(location -> !location.isBlank()).orElse("Maroc"),
                progress,
                progress + "% de l'objectif",
                formatMad(action.getSommeActuelle()),
                formatMad(action.getObjectifCollect())
        );
    }

    private HomeOrganisationView toHomeOrganisationView(Organisation organisation) {
        return new HomeOrganisationView(
                organisation.getId(),
                organisation.getNom(),
                Optional.ofNullable(organisation.getDescription()).filter(description -> !description.isBlank()).orElse("Association engagee pour des actions solidaires a impact concret."),
                Optional.ofNullable(organisation.getLogo()).filter(logo -> !logo.isBlank()).orElse("/images/logo-charite.svg"),
                extractCity(organisation),
                countVisibleActions(organisation)
        );
    }

    private long countVisibleActions(Organisation organisation) {
        if (organisation.getActions() == null) {
            return 0;
        }
        return organisation.getActions().stream()
                .filter(this::isPublicAction)
                .count();
    }

    private int computeProgress(ActionCharite action) {
        if (action.getObjectifCollect() == null || action.getObjectifCollect() <= 0 || action.getSommeActuelle() == null) {
            return 0;
        }
        return (int) Math.max(0, Math.min(100, Math.round((action.getSommeActuelle() / action.getObjectifCollect()) * 100)));
    }

    private String formatMad(Double amount) {
        double value = amount != null ? amount : 0.0;
        return String.format(Locale.US, "%,.0f MAD", value);
    }

    private String resolveActionImage(ActionCharite action) {
        String category = Optional.ofNullable(action.getCategorie()).map(Categorie::getNom).orElse("").toLowerCase(Locale.ROOT);
        if (category.contains("educ")) return "/images/project-school.svg";
        if (category.contains("sant")) return "/images/project-medical.svg";
        if (category.contains("environ")) return "/images/project-trees.svg";
        if (category.contains("social")) return "/images/project-family.svg";
        if (category.contains("urgence")) return "/images/project-meal.svg";
        return "/images/project-library.svg";
    }

    private String resolveCategoryIcon(String name) {
        String normalized = name == null ? "" : name.toLowerCase(Locale.ROOT);
        if (normalized.contains("educ")) return "bi bi-book";
        if (normalized.contains("sant")) return "bi bi-heart-pulse";
        if (normalized.contains("environ")) return "bi bi-leaf";
        if (normalized.contains("social")) return "bi bi-people";
        if (normalized.contains("urgence")) return "bi bi-house-heart";
        return "bi bi-grid";
    }

    private String resolveCategoryColor(String name) {
        String normalized = name == null ? "" : name.toLowerCase(Locale.ROOT);
        if (normalized.contains("educ")) return "is-green";
        if (normalized.contains("sant")) return "is-red";
        if (normalized.contains("environ")) return "is-lime";
        if (normalized.contains("social")) return "is-amber";
        if (normalized.contains("urgence")) return "is-violet";
        return "is-green";
    }

    private String extractCity(Organisation organisation) {
        String address = Optional.ofNullable(organisation.getAdresse()).orElse("").trim();
        if (address.isBlank()) {
            return "Maroc";
        }
        String[] parts = address.split(",");
        return parts[0].trim();
    }
}
