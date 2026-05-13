package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.MethodePaiement;
import ma.emsi.gestionactioncharite.entity.Participation;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import ma.emsi.gestionactioncharite.service.DonService;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.CategorieService;
import ma.emsi.gestionactioncharite.service.ParticipationService;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionChariteService;
    private final OrganisationService organisationService;
    private final CategorieService categorieService;
    private final ParticipationService participationService;
    private final DonService donService;
    private final UserService userService;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping
    public String findAll(Model model, @AuthenticationPrincipal UserDetails principal) {
        boolean isAdmin = isAdmin(principal);
        Long userOrgId = resolveUserOrgId(principal, isAdmin);

        var actions = actionChariteService.findAll().stream()
                .filter(a -> a.getStatus() != StatutAction.BROUILLON
                          || isAdmin
                          || (userOrgId != null && a.getOrganisation() != null
                              && userOrgId.equals(a.getOrganisation().getId())))
                .toList();

        Map<Long, Double> progression = new HashMap<>();
        for (var a : actions) {
            if (a != null && a.getId() != null) {
                progression.put(a.getId(), actionChariteService.getProgression(a.getId()));
            }
        }

        model.addAttribute("actions", actions);
        model.addAttribute("progression", progression);
        model.addAttribute("userOrgId", userOrgId);
        model.addAttribute("isAdmin", isAdmin);
        return "action/list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal UserDetails principal,
                           @RequestParam(required = false) String don) {
        var actionOpt = actionChariteService.findById(id);
        if (actionOpt.isEmpty()) {
            return "action/detail";
        }

        var action = actionOpt.get();
        boolean isAdmin = isAdmin(principal);
        Long userOrgId = resolveUserOrgId(principal, isAdmin);
        boolean canManage = isAdmin
                || (userOrgId != null && action.getOrganisation() != null
                    && userOrgId.equals(action.getOrganisation().getId()));

        // Block access to draft actions for non-owners
        if (action.getStatus() == StatutAction.BROUILLON && !canManage) {
            return "redirect:/actions";
        }

        double objectif = action.getObjectifCollect() != null ? action.getObjectifCollect() : 0.0;
        double collecte = action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0;

        model.addAttribute("action", action);
        model.addAttribute("canManage", canManage);
        model.addAttribute("progression", actionChariteService.getProgression(id));
        model.addAttribute("nbParticipants", participationService.findByActionChariteId(id).size());
        model.addAttribute("resteACollecte", Math.max(0.0, objectif - collecte));
        model.addAttribute("objectifAtteint", collecte >= objectif && objectif > 0);

        List<Don> dons = donService.findByActionChariteId(id);
        model.addAttribute("nbDons", dons.size());
        model.addAttribute("nbDonsStripe", dons.stream()
                .filter(d -> d.getMethodePaiement() == MethodePaiement.STRIPE).count());
        model.addAttribute("nbDonsPaypal", dons.stream()
                .filter(d -> d.getMethodePaiement() == MethodePaiement.PAYPAL).count());

        if (principal != null) {
            var user = userService.findEntityByEmail(principal.getUsername());
            model.addAttribute("dejaInscrit",
                    participationService.findByUserId(user.getId()).stream()
                            .anyMatch(p -> p.getActionCharite().getId().equals(id)));

            List<Don> userDons = donService.findByUserId(user.getId());
            model.addAttribute("dejaPayeStripe", userDons.stream()
                    .anyMatch(d -> d.getActionCharite().getId().equals(id)
                            && d.getMethodePaiement() == MethodePaiement.STRIPE
                            && d.getStatus() == StatutDon.CONFIRME));
            model.addAttribute("dejaPayePaypal", userDons.stream()
                    .anyMatch(d -> d.getActionCharite().getId().equals(id)
                            && d.getMethodePaiement() == MethodePaiement.PAYPAL
                            && d.getStatus() == StatutDon.CONFIRME));
        }
        model.addAttribute("donSuccess", "success".equals(don));
        model.addAttribute("paymentSuccess", "payment-success".equals(don));
        model.addAttribute("donError", "error".equals(don));
        model.addAttribute("paypalError", "paypal-error".equals(don));
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "action/detail";
    }

    @PostMapping("/{id}/participer")
    @PreAuthorize("isAuthenticated()")
    public String participer(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable : " + id));

        // Block inscription when fundraising goal is reached
        double objectif = action.getObjectifCollect() != null ? action.getObjectifCollect() : 0.0;
        double collecte = action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0;
        if (objectif > 0 && collecte >= objectif) {
            return "redirect:/actions/" + id;
        }

        var user = userService.findEntityByEmail(principal.getUsername());
        var participation = Participation.builder()
                .user(user)
                .actionCharite(action)
                .build();
        try {
            participationService.inscrire(participation);
        } catch (RuntimeException ignored) {
        }
        return "redirect:/actions/" + id;
    }

    @PostMapping("/{id}/dons")
    @PreAuthorize("isAuthenticated()")
    public String faireUnDon(@PathVariable Long id,
                             @RequestParam Double montant,
                             @RequestParam MethodePaiement methodePaiement,
                             @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable : " + id));

        double objectif = action.getObjectifCollect() != null ? action.getObjectifCollect() : 0.0;
        double collecte = action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0;
        double reste = Math.max(0.0, objectif - collecte);

        if (objectif > 0 && montant > reste + 0.005) {
            return "redirect:/actions/" + id + "?don=error";
        }

        var user = userService.findEntityByEmail(principal.getUsername());
        var don = Don.builder()
                .user(user)
                .actionCharite(action)
                .montant(montant)
                .methodePaiement(methodePaiement)
                .build();
        donService.save(don);
        return "redirect:/actions/" + id + "?don=success";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("action", new ActionCharite());
        boolean isAdmin = isAdmin(principal);
        Long userOrgId = resolveUserOrgId(principal, isAdmin);
        
        var organisations = organisationService.findAll().stream()
                .filter(o -> o != null && o.getStatus() == StatusOrganisation.APPROVED)
                .filter(o -> isAdmin || (userOrgId != null && userOrgId.equals(o.getId())))
                .toList();
                
        model.addAttribute("organisations", organisations);
        model.addAttribute("categories", categorieService.findAll());
        return "action/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String create(@ModelAttribute ActionCharite action, 
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         @AuthenticationPrincipal UserDetails principal) {
        if (action.getOrganisation() == null || action.getOrganisation().getId() == null) {
            return "redirect:/actions/create?error=missing_org";
        }
        
        boolean isAdmin = isAdmin(principal);
        if (!isAdmin) {
            Long userOrgId = resolveUserOrgId(principal, false);
            if (userOrgId == null || !userOrgId.equals(action.getOrganisation().getId())) {
                return "redirect:/actions?error=unauthorized";
            }
        }
        
        actionChariteService.save(action, imageFile);
        return "redirect:/actions";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id).orElse(null);
        if (action == null) return "redirect:/actions";
        if (!canManageAction(action, principal)) return "redirect:/actions";
        model.addAttribute("action", action);
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("categories", categorieService.findAll());
        return "action/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String update(@PathVariable Long id, 
                         @ModelAttribute ActionCharite action,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         @AuthenticationPrincipal UserDetails principal) {
        var existing = actionChariteService.findById(id).orElse(null);
        if (existing == null || !canManageAction(existing, principal)) return "redirect:/actions";
        actionChariteService.update(id, action, imageFile);
        return "redirect:/actions";
    }

    @GetMapping("/publier/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String publier(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id).orElse(null);
        if (action == null || !canManageAction(action, principal)) return "redirect:/actions";
        actionChariteService.publier(id);
        return "redirect:/actions";
    }

    @GetMapping("/archiver/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String archiver(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id).orElse(null);
        if (action == null || !canManageAction(action, principal)) return "redirect:/actions";
        actionChariteService.archiver(id);
        return "redirect:/actions";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        var action = actionChariteService.findById(id).orElse(null);
        if (action == null || !canManageAction(action, principal)) return "redirect:/actions";
        actionChariteService.delete(id);
        return "redirect:/actions";
    }

    private boolean isAdmin(UserDetails principal) {
        return principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long resolveUserOrgId(UserDetails principal, boolean isAdmin) {
        if (principal == null || isAdmin) return null;
        var user = userService.findEntityByEmail(principal.getUsername());
        return organisationService.findFirstByAdminId(user.getId())
                .map(org -> org.getId())
                .orElse(null);
    }

    private boolean canManageAction(ActionCharite action, UserDetails principal) {
        if (isAdmin(principal)) return true;
        Long userOrgId = resolveUserOrgId(principal, false);
        return userOrgId != null && action.getOrganisation() != null
                && userOrgId.equals(action.getOrganisation().getId());
    }
}
